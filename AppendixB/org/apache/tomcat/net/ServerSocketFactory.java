/*
 * $Header: /CVS/ISNetworks/Books/crypto/Chapters/Chapter\04018/code/org/apache/tomcat/net/ServerSocketFactory.java,v 1.1 2000/10/11 21:01:25 garms Exp $
 * $Revision: 1.1 $
 * $Date: 2000/10/11 21:01:25 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */


package org.apache.tomcat.net;

import java.io.*;
import java.net.*;

/**
 * This class creates server sockets.  It may be subclassed by other
 * factories, which create particular types of server sockets.  This
 * provides a general framework for the addition of public socket-level
 * functionality.  It it is the server side analogue of a socket factory,
 * and similarly provides a way to capture a variety of policies related
 * to the sockets being constructed.
 *
 * <P> Like socket factories, Server Socket factory instances have two
 * categories of methods.  First are methods used to create sockets.
 * Second are methods which set properties used in the production of
 * sockets, such as networking options.  There is also an environment
 * specific default server socket factory; frameworks will often use
 * their own customized factory.
 *
 * <P><hr><em> It may be desirable to move this interface into the
 * <b>java.net</b> package, so that is not an extension but the preferred
 * interface.  Should this be serializable, making it a JavaBean which can
 * be saved along with its networking configuration?
 * </em>
 *
 * @author db@eng.sun.com
 * @author Harish Prabandham
 */


//
// WARNING: Some of the APIs in this class are used by J2EE.
// Please talk to harishp@eng.sun.com before making any changes.
//
public abstract class ServerSocketFactory implements Cloneable {

    //
    // NOTE:  JDK 1.1 bug in class GC, this can get collected
    // even though it's always accessible via getDefault().
    //

    private static ServerSocketFactory theFactory;

    /**
     * Constructor is used only by subclasses.
     */
    protected ServerSocketFactory () {
        /* NOTHING */
    }

    /**
     * Returns a copy of the environment's default socket factory.
     */

    public static ServerSocketFactory getDefault () {
        //
        // optimize typical case:  no synch needed
        //

        if (theFactory == null) {
            synchronized (ServerSocketFactory.class) {
                //
                // Different implementations of this method could
                // work rather differently.  For example, driving
                // this from a system property, or using a different
                // implementation than JavaSoft's.
                //

                //theFactory = new DefaultServerSocketFactory ();
                try {
                  theFactory = (ServerSocketFactory)Class.forName(
                  System.getProperty("tomcat.serverSocketFactory")).
                  newInstance();
                } catch (Exception e) {
                  //Since we don't have a legit factory, die with an error
                  System.err.println("Illegal ServerSocketFactory class " +
                    System.getProperty("tomcat.serverSocketFactory") +
                    ". Could not start.");
                  e.printStackTrace();
                  System.exit(1);
                }

            }
        }

        try {
            return (ServerSocketFactory) theFactory.clone ();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException (e.getMessage ());
        }
    }

    /**
     * Returns a server socket which uses all network interfaces on
     * the host, and is bound to a the specified port.  The socket is
     * configured with the socket options (such as accept timeout)
     * given to this factory.
     *
     * @param port the port to listen to
     * @exception IOException for networking errors
     * @exception InstantiationException for construction errors
     */

    public abstract ServerSocket createSocket (int port)
    throws IOException, InstantiationException;

    /**
     * Returns a server socket which uses all network interfaces on
     * the host, is bound to a the specified port, and uses the
     * specified connection backlog.  The socket is configured with
     * the socket options (such as accept timeout) given to this factory.
     *
     * @param port the port to listen to
     * @param backlog how many connections are queued
     * @exception IOException for networking errors
     * @exception InstantiationException for construction errors
     */

    public abstract ServerSocket createSocket (int port, int backlog)
    throws IOException, InstantiationException;

    /**
     * Returns a server socket which uses only the specified network
     * interface on the local host, is bound to a the specified port,
     * and uses the specified connection backlog.  The socket is configured
     * with the socket options (such as accept timeout) given to this factory.
     *
     * @param port the port to listen to
     * @param backlog how many connections are queued
     * @param ifAddress the network interface address to use
     * @exception IOException for networking errors
     * @exception InstantiationException for construction errors
     */

    public abstract ServerSocket createSocket (int port,
        int backlog, InetAddress ifAddress)
    throws IOException, InstantiationException;
}

