package ch.zir.ffv.viz.app;

import io.dropwizard.Application;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;

public class FfvVizApplication extends Application<FfvVizConfiguration> {

	// private VerbStore store;

	@Override
	public String getName() {
		return "ffv-viz";
	}

	@Override
	public void initialize(final Bootstrap<FfvVizConfiguration> bootstrap) {
		super.initialize(bootstrap);
		bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/", "index.html"));

		// store = new VerbStore();
		// store.load();
	}

	@Override
	public void run(final FfvVizConfiguration configuration, final Environment environment) throws Exception {
		// see https://groups.google.com/forum/#!topic/dropwizard-user/5qZYhirC--w
		((DefaultServerFactory) configuration.getServerFactory()).setJerseyRootPath("/api/*");

		// final VerbsResource verbsResource = new VerbsResource(store);
		// environment.jersey().register(verbsResource);
		//
		// final TenseResource tenseResource = new TenseResource();
		// environment.jersey().register(tenseResource);

		// TemplateHealthCheck healthCheck = new
		// TemplateHealthCheck(configuration.getTemplate());
		// environment.healthChecks().register("template", healthCheck);
	}

	public static void main(final String[] args) throws Exception {
		new FfvVizApplication().run(args);
	}

}
