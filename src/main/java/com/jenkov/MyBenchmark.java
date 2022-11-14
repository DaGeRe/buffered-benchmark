/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jenkov;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

class GraphElement implements Serializable {
   private static final long serialVersionUID = 3191704325571191609L;
   private String name;
   List<GraphElement> neighbours = new LinkedList<>();

   public GraphElement(String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }
}

@State(Scope.Thread)
public class MyBenchmark {

   GraphElement routeNet;

   @Setup
   public void initGraph() {
      routeNet = new GraphElement("root");
      for (int i = 0; i < 1000; i++) {
         GraphElement element = new GraphElement("A" + i);
         routeNet.neighbours.add(element);
         for (int j = 0; j < 1000; j++) {
            GraphElement neighbourOfNeighbour = new GraphElement("B" + j);
            element.neighbours.add(neighbourOfNeighbour);
         }
      }
   }

   @Benchmark
   public void testWithBufferedStream() {
      File destFile = new File("/dev/null");
      
      try (OutputStream stream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(destFile)));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(stream))) {
         objectOutputStream.writeObject(routeNet);
         objectOutputStream.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   @Benchmark
   public void testWithoutDoubleBufferedStream() {
      File destFile = new File("/dev/null");
      
      try (OutputStream stream = new GZIPOutputStream(new FileOutputStream(destFile));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(stream))) {
         objectOutputStream.writeObject(routeNet);
         objectOutputStream.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   @Benchmark
   public void testWithoutBufferedStream() {
      File destFile = new File("/dev/null");
      
      try (OutputStream stream = new GZIPOutputStream(new FileOutputStream(destFile));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream)) {
         objectOutputStream.writeObject(routeNet);
         objectOutputStream.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
