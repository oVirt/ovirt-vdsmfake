package org.ovirt.vdsmfake.it;

import java.util.function.Function;

import org.codehaus.jackson.JsonNode;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.TypesafeAppConfigProvider;

import com.netflix.hystrix.Hystrix;
import com.typesafe.config.ConfigFactory;

public interface IntegrationTest {

    static JavaArchive shrinkWrap() {
        return ShrinkWrap.create(JavaArchive.class);
    }

    static Function<JavaArchive, JavaArchive> withEmptyBeansXml() {
        return archive -> archive
                .addAsManifestResource(
                        EmptyAsset.INSTANCE,
                        ArchivePaths.create("beans.xml"));
    }

    static Function<JavaArchive, JavaArchive> withConfigArtifacts() {
        return archive-> archive
                .addClass(AppConfig.class)
                .addClass(TypesafeAppConfigProvider.class)
                .addPackages(true, ConfigFactory.class.getPackage())
                .addAsResource("application.conf");
    }

    static Function<JavaArchive, JavaArchive> withCommands() {
        return archive -> archive
                .addPackages(true, "org.ovirt.vdsm")
                .addPackages(true, "org.ovirt.vdsmfake")
                .addPackages(true, JsonNode.class.getPackage())
                .addPackage(Hystrix.class.getPackage());
    }

    static JavaArchive deployAll() {
        return withConfigArtifacts()
                .andThen(withCommands())
                .andThen(withEmptyBeansXml())
                .apply(shrinkWrap());

    }
}
