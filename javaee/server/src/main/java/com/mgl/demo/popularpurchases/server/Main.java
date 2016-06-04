package com.mgl.demo.popularpurchases.server;

import java.net.URL;

import com.google.common.base.Preconditions;
import org.wildfly.swarm.Swarm;

public class Main {

    public static void main(String[] args) throws Exception {

        ClassLoader cl = Main.class.getClassLoader();
        URL stageConfig = cl.getResource("project-stages.yml");

        Preconditions.checkNotNull(stageConfig, "stageConfig");

        Swarm swarm = new Swarm(false, args)
                .withStageConfig(stageConfig);

        swarm.start();

        swarm.deploy();
    }
}
