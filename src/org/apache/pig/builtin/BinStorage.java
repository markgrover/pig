/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pig.builtin;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.pig.ExecType;
import org.apache.pig.ReversibleLoadStoreFunc;
import org.apache.pig.backend.datastorage.DataStorage;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.DataReaderWriter;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.io.BufferedPositionedInputStream;
import org.apache.pig.impl.io.FileLocalizer;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.SchemaMergeException;
import org.apache.pig.impl.util.WrappedIOException;


public class BinStorage implements ReversibleLoadStoreFunc {
    public static final byte RECORD_1 = 0x01;
    public static final byte RECORD_2 = 0x02;
    public static final byte RECORD_3 = 0x03;

    Iterator<Tuple>     i              = null;
    protected BufferedPositionedInputStream in = null;
    private DataInputStream inData = null;
    protected long                end            = Long.MAX_VALUE;
    
    /**
     * Simple binary nested reader format
     */
    public BinStorage() {
    }

    public Tuple getNext() throws IOException {
        
        byte b = 0;
//      skip to next record
        while (true) {
            if (in == null || in.getPosition() >=end) {
                return null;
            }
            b = (byte) in.read();
            if(b != RECORD_1 && b != -1) {
                continue;
            }
            if(b == -1) return null;
            b = (byte) in.read();
            if(b != RECORD_2 && b != -1) {
                continue;
            }
            if(b == -1) return null;
            b = (byte) in.read();
            if(b != RECORD_3 && b != -1) {
                continue;
            }
            if(b == -1) return null;
            break;
        }
        try {
            return (Tuple)DataReaderWriter.readDatum(inData);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            oughtToBeEE.initCause(ee);
            throw oughtToBeEE;
        }
    }

    public void bindTo(String fileName, BufferedPositionedInputStream in, long offset, long end) throws IOException {
        this.in = in;
        inData = new DataInputStream(in);
        this.end = end;
    }


    DataOutputStream         out     = null;
  
    public void bindTo(OutputStream os) throws IOException {
        this.out = new DataOutputStream(new BufferedOutputStream(os));
    }

    public void finish() throws IOException {
        out.flush();
    }

    public void putNext(Tuple t) throws IOException {
        out.write(RECORD_1);
        out.write(RECORD_2);
        out.write(RECORD_3);
        t.write(out);
    }

    public DataBag bytesToBag(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (DataBag)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }        
    }

    public String bytesToCharArray(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (String)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
    }

    public Double bytesToDouble(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (Double)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
    }

    public Float bytesToFloat(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (Float)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
    }

    public Integer bytesToInteger(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (Integer)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
    }

    public Long bytesToLong(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (Long)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
    }

    public Map<Object, Object> bytesToMap(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (Map<Object, Object>)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
    }

    public Tuple bytesToTuple(byte[] b) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
        try {
            return (Tuple)DataReaderWriter.readDatum(dis);
        } catch (ExecException ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.pig.LoadFunc#determineSchema(java.lang.String, org.apache.pig.ExecType, org.apache.pig.backend.datastorage.DataStorage)
     */
    public Schema determineSchema(String fileName, ExecType execType,
            DataStorage storage) throws IOException {

        InputStream is = null;

        try {
            is = FileLocalizer.open(fileName, execType, storage);
        } catch (IOException e) {
            // At compile time in batch mode, the file may not exist
            // (such as intermediate file). Just return null - the
            // same way as we could's get a valid record from the input.
            return null;
        }
        
        bindTo(fileName, new BufferedPositionedInputStream(is), 0, Long.MAX_VALUE);
        // get the first record from the input file
        // and figure out the schema from the data in
        // the first record
        Tuple t = getNext();
        is.close();
        if(t == null) {
            // we couldn't get a valid record from the input
            return null;
        }
        int numFields = t.size();
        Schema s = new Schema();
        for (int i = 0; i < numFields; i++) {
            try {
                s.add(DataType.determineFieldSchema(t.get(i)));
            } catch (Exception e) {
                throw WrappedIOException.wrap(e);
            } 
        }
        return s;
    }

    public void fieldsToRead(Schema schema) {
        // TODO Auto-generated method stub
        
    }

    public byte[] toBytes(DataBag bag) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, bag);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }

    public byte[] toBytes(String s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, s);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }

    public byte[] toBytes(Double d) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, d);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }

    public byte[] toBytes(Float f) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, f);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }

    public byte[] toBytes(Integer i) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, i);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }

    public byte[] toBytes(Long l) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, l);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }

    public byte[] toBytes(Map<Object, Object> m) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, m);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }

    public byte[] toBytes(Tuple t) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            DataReaderWriter.writeDatum(dos, t);
        } catch (Exception ee) {
            IOException oughtToBeEE = new IOException();
            ee.initCause(ee);
            throw oughtToBeEE;
        }
        return baos.toByteArray();
    }
    public boolean equals(Object obj) {
        return true;
    }
}
