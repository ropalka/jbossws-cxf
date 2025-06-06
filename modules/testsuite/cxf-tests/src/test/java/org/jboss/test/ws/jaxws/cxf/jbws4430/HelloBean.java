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

import jakarta.jws.HandlerChain;
import org.apache.cxf.interceptor.OutInterceptors;

@jakarta.jws.WebService(targetNamespace = "http://test.ws.jboss.org/",
        wsdlLocation = "WEB-INF/wsdl/HelloWorld.wsdl")
@HandlerChain(file = "/handlers.xml")
@OutInterceptors(interceptors = {"org.jboss.test.ws.jaxws.cxf.jbws4430.CDIOutInterceptor"})
public class HelloBean {
    public HelloBean() {
    }

    @jakarta.jws.WebMethod
    public String hello(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Empty name");
        }
        return "Hello " + name;
    }
}
