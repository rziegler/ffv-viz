package ch.zir.ffv.viz.app;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import ch.zir.ffv.viz.app.jdbi.DbAccess;
import ch.zir.ffv.viz.app.resource.FlightResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class FfvVizApplication extends Application<FfvVizConfiguration> {

	// private VerbStore store;

	@Override
	public String getName() {
		return "ffv-viz";
	}

	@Override
	public void initialize(final Bootstrap<FfvVizConfiguration> bootstrap) {
		super.initialize(bootstrap);
		// bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/", "index.html"));

	}

	@Override
	public void run(final FfvVizConfiguration config, final Environment environment) throws Exception {
		// see https://groups.google.com/forum/#!topic/dropwizard-user/5qZYhirC--w
		// ((DefaultServerFactory) config.getServerFactory()).setJerseyRootPath("/api/*");

		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment, config.getDataSourceFactory(), "mariadb");
		final DbAccess dao = jdbi.onDemand(DbAccess.class);
		environment.jersey().register(new FlightResource(dao));

		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		// Add URL mapping
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");

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
