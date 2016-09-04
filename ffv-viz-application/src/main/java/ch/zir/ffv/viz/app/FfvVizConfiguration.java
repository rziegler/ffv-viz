package ch.zir.ffv.viz.app;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class FfvVizConfiguration extends Configuration implements AssetsBundleConfiguration {

	Logger log = LoggerFactory.getLogger(FfvVizConfiguration.class);

	@Valid
	@NotNull
	@JsonProperty
	private final AssetsConfiguration assets = new AssetsConfiguration();

	@Override
	public AssetsConfiguration getAssetsConfiguration() {
		log.debug("Using assets configuration: " + Iterables.toString(assets.getOverrides()));
		return assets;
	}

	@Valid
	@NotNull
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();

	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

}
