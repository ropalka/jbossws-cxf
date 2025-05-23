/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.test.ws.jaxws.cxf.jbws4430;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.SOAPFaultException;

@ExtendWith(ArquillianExtension.class)
public class JBWS4430TestCase extends JBossWSTest {
    private static final String DEP = "jaxws-cxf-jbws4430";

    @ArquillianResource
    private URL baseURL;

    @Deployment(name = DEP, testable = false)
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, DEP + ".war");
        archive.setManifest(new StringAsset(
                "Manifest-Version: 1.0\n" + "Dependencies: org.apache.cxf,org.jboss.ws.cxf.jbossws-cxf-server\n"))
                .addClasses(ClientBean.class, Hello.class, HelloBean.class, DelegateBean.class, EmptyBean.class, CDIOutInterceptor.class,
                        LoggingHandler.class, AccessTokenClientHandler.class, AccessTokenClientHandlerResolver.class,
                        CredentialsCDIBean.class)
                .addAsWebInfResource(
                        new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jbws4430/WEB-INF/beans.xml"),
                        "beans.xml")
                .addAsWebInfResource(
                        new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jbws4430/WEB-INF/wsdl/HelloWorld.wsdl"),
                        "wsdl/HelloWorld.wsdl")
                .addAsWebInfResource(new File(
                        JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jbws4430/WEB-INF/wsdl/ClientBeanService.wsdl"),
                        "wsdl/ClientBeanService.wsdl")
                .add(new FileAsset(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jbws4430/handlers.xml")),
                        "WEB-INF/classes/handlers.xml");
        return archive;
    }

    @Test
    @RunAsClient
    public void testWS() throws Exception {
        QName serviceName = new QName("http://test.ws.jboss.org/", "HelloBeanService");
        QName portName = new QName("http://test.ws.jboss.org/", "HelloBeanPort");

        URL wsdlURL = new URL(baseURL + "HelloBean?wsdl");

        Service service = Service.create(wsdlURL, serviceName);
        Hello proxy = service.getPort(portName, Hello.class);
        Assertions.assertEquals("Hello jbossws", proxy.hello("jbossws"));
        try {
            proxy.hello("");
            fail("An exception is expected to test the LoggingHandler.handleFault()");
        } catch (Exception e) {
            Assertions.assertInstanceOf(SOAPFaultException.class, e, "unexpected exception");
        }

    }

    @Test
    @RunAsClient
    public void testClientWS() throws Exception {
        QName serviceName = new QName("http://test.ws.jboss.org/", "ClientBeanService");
        QName portName = new QName("http://test.ws.jboss.org/", "ClientBeanPort");

        URL wsdlURL = new URL(baseURL + "ClientBeanService?wsdl");

        Service service = Service.create(wsdlURL, serviceName);
        Client proxy = service.getPort(portName, Client.class);
        Assertions.assertEquals("Hello jbossws", proxy.hello(baseURL, "jbossws"));
    }
}