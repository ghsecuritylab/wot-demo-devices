package ch.ethz.inf.vs.wot.demo.services.resources;

import ch.ethz.inf.vs.wot.demo.services.LIFX;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.TEXT_PLAIN;

public class PowerRelay extends CoapResource {
	
	private static boolean on = false;
	private final LIFX lifx;

	public static boolean getRelay() {
		return on;
	}

	public PowerRelay(LIFX lifx) {
		super("switch");
		this.lifx = lifx;
		getAttributes().setTitle("On/off switch");
		getAttributes().addResourceType("switch");
		getAttributes().addInterfaceDescription("core#a");
		getAttributes().setObservable();

		setObservable(true);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(CONTENT, on ? "1":"0", TEXT_PLAIN);
	}
	
	@Override
	public void handlePUT(CoapExchange exchange) {

		if (!exchange.getRequestOptions().isContentFormat(TEXT_PLAIN)) {
			exchange.respond(BAD_REQUEST, "text/plain only");
			return;
		}
		
		String pl = exchange.getRequestText();
		if (pl.equals("true") || pl.equals("on") || pl.equals("1")) {
			on = true;
		} else if (pl.equals("false") || pl.equals("off") || pl.equals("0")) {
			on = false;
		} else {
			exchange.respond(BAD_REQUEST, "use true/false, on/off, or 1/0");
			return;
		}
		
		lifx.bulb.setPower(on, 0);
		
		// complete the request
		exchange.respond(CHANGED);
		
		changed();
	}
}
