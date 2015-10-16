package ch.ethz.inf.vs.wot.demo.services.resources;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;

public class PowerInstantaneous extends CoapResource {
	
	private static double power = 0d;
	
	public static double getPower() {
		return power;
	}

	public PowerInstantaneous() {
		super("w");
		getAttributes().setTitle("Instantaneous Power");
		getAttributes().addResourceType("ucum:W");
		getAttributes().addInterfaceDescription("core#rp");
		getAttributes().setObservable();

		setObservable(true);

		// Set timer task scheduling
		Timer timer = new Timer();
		timer.schedule(new TimeTask(), 0, 1000);
	}

	private class TimeTask extends TimerTask {

		@Override
		public void run() {
			if (PowerRelay.getRelay()) {
				power = Math.round( 200d + 5d*Math.random() ) / 1000f;
			} else {
				// skip changed() update if nothing changed
				if (power==0d) {
					return;
				}
				
				power = 0d;
			}

			// Call changed to notify subscribers
			changed();
		}
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(CONTENT, new DecimalFormat("##.00").format(power), TEXT_PLAIN);
	}
}
