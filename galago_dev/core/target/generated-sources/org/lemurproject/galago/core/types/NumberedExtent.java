// This file was automatically generated with the by org.lemurproject.galago.tupleflow.typebuilder.TypeBuilderMojo ...
package org.lemurproject.galago.core.types;

import org.lemurproject.galago.tupleflow.*;
import org.lemurproject.galago.tupleflow.protocol.*;
import org.lemurproject.galago.tupleflow.error.*;
import org.lemurproject.galago.utility.*;
import java.io.*;
import java.util.*;
import gnu.trove.list.array.*;

/**
 * Tupleflow-Typebuilder automatically-generated class: NumberedExtent.
 */
@SuppressWarnings("unused")
public final class NumberedExtent implements Type<NumberedExtent> {
    public byte[] extentName;
    public long number;
    public int begin;
    public int end; 
    
    /** default constructor makes most fields null */
    public NumberedExtent() {}
    /** additional constructor takes all fields explicitly */
    public NumberedExtent(byte[] extentName, long number, int begin, int end) {
        this.extentName = extentName;
        this.number = number;
        this.begin = begin;
        this.end = end;
    }  
    
    public String toString() {
        try {
            return String.format("%s,%d,%d,%d",
                                   new String(extentName, "UTF-8"), number, begin, end);
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException("Couldn't convert string to UTF-8.");
        }
    } 

    public Order<NumberedExtent> getOrder(String... spec) {
        if (Arrays.equals(spec, new String[] { "+number", "+begin" })) {
            return new NumberBeginOrder();
        }
        if (Arrays.equals(spec, new String[] { "+extentName", "+number", "+begin" })) {
            return new ExtentNameNumberBeginOrder();
        }
        if (Arrays.equals(spec, new String[] { "+extentName" })) {
            return new ExtentNameOrder();
        }
        return null;
    } 
      
    public interface Processor extends Step, org.lemurproject.galago.tupleflow.Processor<NumberedExtent> {
        public void process(NumberedExtent object) throws IOException;
    } 
    public interface Source extends Step {
    }
    public static final class NumberBeginOrder implements Order<NumberedExtent> {
        public int hash(NumberedExtent object) {
            int h = 0;
            h += CmpUtil.hash(object.number);
            h += CmpUtil.hash(object.begin);
            return h;
        } 
        public Comparator<NumberedExtent> greaterThan() {
            return new Comparator<NumberedExtent>() {
                public int compare(NumberedExtent one, NumberedExtent two) {
                    int result = 0;
                    do {
                        result = + CmpUtil.compare(one.number, two.number);
                        if(result != 0) break;
                        result = + CmpUtil.compare(one.begin, two.begin);
                        if(result != 0) break;
                    } while (false);
                    return -result;
                }
            };
        }     
        public Comparator<NumberedExtent> lessThan() {
            return new Comparator<NumberedExtent>() {
                public int compare(NumberedExtent one, NumberedExtent two) {
                    int result = 0;
                    do {
                        result = + CmpUtil.compare(one.number, two.number);
                        if(result != 0) break;
                        result = + CmpUtil.compare(one.begin, two.begin);
                        if(result != 0) break;
                    } while (false);
                    return result;
                }
            };
        }     
        public TypeReader<NumberedExtent> orderedReader(ArrayInput _input) {
            return new ShreddedReader(_input);
        }    

        public TypeReader<NumberedExtent> orderedReader(ArrayInput _input, int bufferSize) {
            return new ShreddedReader(_input, bufferSize);
        }    
        public OrderedWriter<NumberedExtent> orderedWriter(ArrayOutput _output) {
            ShreddedWriter w = new ShreddedWriter(_output);
            return new OrderedWriterClass(w); 
        }                                    
        public static final class OrderedWriterClass extends OrderedWriter< NumberedExtent > {
            NumberedExtent last = null;
            ShreddedWriter shreddedWriter = null; 
            
            public OrderedWriterClass(ShreddedWriter s) {
                this.shreddedWriter = s;
            }
            
            public void process(NumberedExtent object) throws IOException {
               boolean processAll = false;
               if (processAll || last == null || 0 != CmpUtil.compare(object.number, last.number)) { processAll = true; shreddedWriter.processNumber(object.number); }
               if (processAll || last == null || 0 != CmpUtil.compare(object.begin, last.begin)) { processAll = true; shreddedWriter.processBegin(object.begin); }
               shreddedWriter.processTuple(object.extentName, object.end);
               last = object;
            }           

            @Override
            public void close() throws IOException {
                shreddedWriter.close();
            }
            
            public Class<NumberedExtent> getInputClass() {
                return NumberedExtent.class;
            }
        } 
        public ReaderSource<NumberedExtent> orderedCombiner(Collection<TypeReader<NumberedExtent>> readers, boolean closeOnExit) {
            ArrayList<ShreddedReader> shreddedReaders = new ArrayList<ShreddedReader>();
            
            for (TypeReader<NumberedExtent> reader : readers) {
                shreddedReaders.add((ShreddedReader)reader);
            }
            
            return new ShreddedCombiner(shreddedReaders, closeOnExit);
        }                  
        public NumberedExtent clone(NumberedExtent object) {
            NumberedExtent result = new NumberedExtent();
            if (object == null) return result;
            result.extentName = object.extentName; 
            result.number = object.number; 
            result.begin = object.begin; 
            result.end = object.end; 
            return result;
        }                 
        public Class<NumberedExtent> getOrderedClass() {
            return NumberedExtent.class;
        }                           
        public String[] getOrderSpec() {
            return new String[] {"+number", "+begin"};
        }

        public static String[] getSpec() {
            return new String[] {"+number", "+begin"};
        }
        public static String getSpecString() {
            return "+number +begin";
        }
                           
        public interface ShreddedProcessor extends Step, Closeable {
            public void processNumber(long number) throws IOException;
            public void processBegin(int begin) throws IOException;
            public void processTuple(byte[] extentName, int end) throws IOException;
        } 

        public static final class ShreddedWriter implements ShreddedProcessor {
            ArrayOutput output;
            ShreddedBuffer buffer = new ShreddedBuffer();
            long lastNumber;
            int lastBegin;
            boolean lastFlush = false;
            
            public ShreddedWriter(ArrayOutput output) {
                this.output = output;
            }                        

            @Override
            public void close() throws IOException {
                flush();
            }
            
            public void processNumber(long number) {
                lastNumber = number;
                buffer.processNumber(number);
            }
            public void processBegin(int begin) {
                lastBegin = begin;
                buffer.processBegin(begin);
            }
            public final void processTuple(byte[] extentName, int end) throws IOException {
                if (lastFlush) {
                    if(buffer.numbers.size() == 0) buffer.processNumber(lastNumber);
                    if(buffer.begins.size() == 0) buffer.processBegin(lastBegin);
                    lastFlush = false;
                }
                buffer.processTuple(extentName, end);
                if (buffer.isFull())
                    flush();
            }
            public final void flushTuples(int pauseIndex) throws IOException {
                
                while (buffer.getReadIndex() < pauseIndex) {
                           
                    output.writeBytes(buffer.getExtentName());
                    output.writeInt(buffer.getEnd());
                    buffer.incrementTuple();
                }
            }  
            public final void flushNumber(int pauseIndex) throws IOException {
                while (buffer.getReadIndex() < pauseIndex) {
                    int nextPause = buffer.getNumberEndIndex();
                    int count = nextPause - buffer.getReadIndex();
                    
                    output.writeLong(buffer.getNumber());
                    output.writeInt(count);
                    buffer.incrementNumber();
                      
                    flushBegin(nextPause);
                    assert nextPause == buffer.getReadIndex();
                }
            }
            public final void flushBegin(int pauseIndex) throws IOException {
                while (buffer.getReadIndex() < pauseIndex) {
                    int nextPause = buffer.getBeginEndIndex();
                    int count = nextPause - buffer.getReadIndex();
                    
                    output.writeInt(buffer.getBegin());
                    output.writeInt(count);
                    buffer.incrementBegin();
                      
                    flushTuples(nextPause);
                    assert nextPause == buffer.getReadIndex();
                }
            }
            public void flush() throws IOException { 
                flushNumber(buffer.getWriteIndex());
                buffer.reset(); 
                lastFlush = true;
            }                           
        }
        public static final class ShreddedBuffer {
            TLongArrayList numbers = new TLongArrayList();
            TIntArrayList begins = new TIntArrayList();
            TIntArrayList numberTupleIdx = new TIntArrayList();
            TIntArrayList beginTupleIdx = new TIntArrayList();
            int numberReadIdx = 0;
            int beginReadIdx = 0;
                            
            byte[][] extentNames;
            int[] ends;
            int writeTupleIndex = 0;
            int readTupleIndex = 0;
            int batchSize;

            public ShreddedBuffer(int batchSize) {
                this.batchSize = batchSize;

                extentNames = new byte[batchSize][];
                ends = new int[batchSize];
            }                              

            public ShreddedBuffer() {    
                this(10000);
            }                                                                                                                    
            
            public void processNumber(long number) {
                numbers.add(number);
                numberTupleIdx.add(writeTupleIndex);
            }                                      
            public void processBegin(int begin) {
                begins.add(begin);
                beginTupleIdx.add(writeTupleIndex);
            }                                      
            public void processTuple(byte[] extentName, int end) {
                assert numbers.size() > 0;
                assert begins.size() > 0;
                extentNames[writeTupleIndex] = extentName;
                ends[writeTupleIndex] = end;
                writeTupleIndex++;
            }
            public void resetData() {
                numbers.clear();
                begins.clear();
                numberTupleIdx.clear();
                beginTupleIdx.clear();
                writeTupleIndex = 0;
            }                  
                                 
            public void resetRead() {
                readTupleIndex = 0;
                numberReadIdx = 0;
                beginReadIdx = 0;
            } 

            public void reset() {
                resetData();
                resetRead();
            } 
            public boolean isFull() {
                return writeTupleIndex >= batchSize;
            }

            public boolean isEmpty() {
                return writeTupleIndex == 0;
            }                          

            public boolean isAtEnd() {
                return readTupleIndex >= writeTupleIndex;
            }           
            public void incrementNumber() {
                numberReadIdx++;  
            }                                                                                              

            public void autoIncrementNumber() {
                while (readTupleIndex >= getNumberEndIndex() && readTupleIndex < writeTupleIndex)
                    numberReadIdx++;
            }                 
            public void incrementBegin() {
                beginReadIdx++;  
            }                                                                                              

            public void autoIncrementBegin() {
                while (readTupleIndex >= getBeginEndIndex() && readTupleIndex < writeTupleIndex)
                    beginReadIdx++;
            }                 
            public void incrementTuple() {
                readTupleIndex++;
            }                    
            public int getNumberEndIndex() {
                if ((numberReadIdx+1) >= numberTupleIdx.size())
                    return writeTupleIndex;
                return numberTupleIdx.get(numberReadIdx+1);
            }

            public int getBeginEndIndex() {
                if ((beginReadIdx+1) >= beginTupleIdx.size())
                    return writeTupleIndex;
                return beginTupleIdx.get(beginReadIdx+1);
            }
            public int getReadIndex() {
                return readTupleIndex;
            }   

            public int getWriteIndex() {
                return writeTupleIndex;
            } 
            public long getNumber() {
                assert readTupleIndex < writeTupleIndex;
                assert numberReadIdx < numbers.size();
                
                return numbers.get(numberReadIdx);
            }
            public int getBegin() {
                assert readTupleIndex < writeTupleIndex;
                assert beginReadIdx < begins.size();
                
                return begins.get(beginReadIdx);
            }
            public byte[] getExtentName() {
                assert readTupleIndex < writeTupleIndex;
                return extentNames[readTupleIndex];
            }                                         
            public int getEnd() {
                assert readTupleIndex < writeTupleIndex;
                return ends[readTupleIndex];
            }                                         
            public void copyTuples(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                   output.processTuple(getExtentName(), getEnd());
                   incrementTuple();
                }
            }                                                                           
            public void copyUntilIndexNumber(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                    output.processNumber(getNumber());
                    assert getNumberEndIndex() <= endIndex;
                    copyUntilIndexBegin(getNumberEndIndex(), output);
                    incrementNumber();
                }
            } 
            public void copyUntilIndexBegin(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                    output.processBegin(getBegin());
                    assert getBeginEndIndex() <= endIndex;
                    copyTuples(getBeginEndIndex(), output);
                    incrementBegin();
                }
            }  
            public void copyUntilNumber(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                while (!isAtEnd()) {
                    if (other != null) {   
                        assert !other.isAtEnd();
                        int c = + CmpUtil.compare(getNumber(), other.getNumber());
                    
                        if (c > 0) {
                            break;   
                        }
                        
                        output.processNumber(getNumber());
                                      
                        if (c < 0) {
                            copyUntilIndexBegin(getNumberEndIndex(), output);
                        } else if (c == 0) {
                            copyUntilBegin(other, output);
                            autoIncrementNumber();
                            break;
                        }
                    } else {
                        output.processNumber(getNumber());
                        copyUntilIndexBegin(getNumberEndIndex(), output);
                    }
                    incrementNumber();  
                    
               
                }
            }
            public void copyUntilBegin(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                while (!isAtEnd()) {
                    if (other != null) {   
                        assert !other.isAtEnd();
                        int c = + CmpUtil.compare(getBegin(), other.getBegin());
                    
                        if (c > 0) {
                            break;   
                        }
                        
                        output.processBegin(getBegin());
                                      
                        copyTuples(getBeginEndIndex(), output);
                    } else {
                        output.processBegin(getBegin());
                        copyTuples(getBeginEndIndex(), output);
                    }
                    incrementBegin();  
                    
                    if (getNumberEndIndex() <= readTupleIndex)
                        break;   
                }
            }
            public void copyUntil(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                copyUntilNumber(other, output);
            }
            
        }                         
        public static final class ShreddedCombiner implements ReaderSource<NumberedExtent>, ShreddedSource {
            public ShreddedProcessor processor;
            Collection<ShreddedReader> readers;       
            boolean closeOnExit = false;
            boolean uninitialized = true;
            PriorityQueue<ShreddedReader> queue = new PriorityQueue<ShreddedReader>();
            
            public ShreddedCombiner(Collection<ShreddedReader> readers, boolean closeOnExit) {
                this.readers = readers;                                                       
                this.closeOnExit = closeOnExit;
            }
                                  
            public void setProcessor(Step processor) throws IncompatibleProcessorException {  
                if (processor instanceof ShreddedProcessor) {
                    this.processor = new DuplicateEliminator((ShreddedProcessor) processor);
                } else if (processor instanceof NumberedExtent.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((NumberedExtent.Processor) processor));
                } else if (processor instanceof org.lemurproject.galago.tupleflow.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((org.lemurproject.galago.tupleflow.Processor<NumberedExtent>) processor));
                } else {
                    throw new IncompatibleProcessorException(processor.getClass().getName() + " is not supported by " + this.getClass().getName());                                                                       
                }
            }                                
            
            public Class<NumberedExtent> getOutputClass() {
                return NumberedExtent.class;
            }
            
            public void initialize() throws IOException {
                for (ShreddedReader reader : readers) {
                    reader.fill();                                        
                    
                    if (!reader.getBuffer().isAtEnd())
                        queue.add(reader);
                }   

                uninitialized = false;
            }

            public void run() throws IOException {
                initialize();
               
                while (queue.size() > 0) {
                    ShreddedReader top = queue.poll();
                    ShreddedReader next = null;
                    ShreddedBuffer nextBuffer = null; 
                    
                    assert !top.getBuffer().isAtEnd();
                                                  
                    if (queue.size() > 0) {
                        next = queue.peek();
                        nextBuffer = next.getBuffer();
                        assert !nextBuffer.isAtEnd();
                    }
                    
                    top.getBuffer().copyUntil(nextBuffer, processor);
                    if (top.getBuffer().isAtEnd())
                        top.fill();                 
                        
                    if (!top.getBuffer().isAtEnd())
                        queue.add(top);
                }              
                
                if (closeOnExit)
                    processor.close();
            }

            public NumberedExtent read() throws IOException {
                if (uninitialized)
                    initialize();

                NumberedExtent result = null;

                while (queue.size() > 0) {
                    ShreddedReader top = queue.poll();
                    result = top.read();

                    if (result != null) {
                        if (top.getBuffer().isAtEnd())
                            top.fill();

                        queue.offer(top);
                        break;
                    } 
                }

                return result;
            }
        } 
        public static final class ShreddedReader implements Step, Comparable<ShreddedReader>, TypeReader<NumberedExtent>, ShreddedSource {
            public ShreddedProcessor processor;
            ShreddedBuffer buffer;
            NumberedExtent last = new NumberedExtent();         
            long updateNumberCount = -1;
            long updateBeginCount = -1;
            long tupleCount = 0;
            long bufferStartCount = 0;  
            ArrayInput input;
            
            public ShreddedReader(ArrayInput input) {
                this.input = input; 
                this.buffer = new ShreddedBuffer();
            }                               
            
            public ShreddedReader(ArrayInput input, int bufferSize) { 
                this.input = input;
                this.buffer = new ShreddedBuffer(bufferSize);
            }
                 
            public final int compareTo(ShreddedReader other) {
                ShreddedBuffer otherBuffer = other.getBuffer();
                
                if (buffer.isAtEnd() && otherBuffer.isAtEnd()) {
                    return 0;                 
                } else if (buffer.isAtEnd()) {
                    return -1;
                } else if (otherBuffer.isAtEnd()) {
                    return 1;
                }
                                   
                int result = 0;
                do {
                    result = + CmpUtil.compare(buffer.getNumber(), otherBuffer.getNumber());
                    if(result != 0) break;
                    result = + CmpUtil.compare(buffer.getBegin(), otherBuffer.getBegin());
                    if(result != 0) break;
                } while (false);                                             
                
                return result;
            }
            
            public final ShreddedBuffer getBuffer() {
                return buffer;
            }                
            
            public final NumberedExtent read() throws IOException {
                if (buffer.isAtEnd()) {
                    fill();             
                
                    if (buffer.isAtEnd()) {
                        return null;
                    }
                }
                      
                assert !buffer.isAtEnd();
                NumberedExtent result = new NumberedExtent();
                
                result.number = buffer.getNumber();
                result.begin = buffer.getBegin();
                result.extentName = buffer.getExtentName();
                result.end = buffer.getEnd();
                
                buffer.incrementTuple();
                buffer.autoIncrementNumber();
                buffer.autoIncrementBegin();
                
                return result;
            }           
            
            public final void fill() throws IOException {
                try {   
                    buffer.reset();
                    
                    if (tupleCount != 0) {
                                                      
                        if(updateNumberCount - tupleCount > 0) {
                            buffer.numbers.add(last.number);
                            buffer.numberTupleIdx.add((int) (updateNumberCount - tupleCount));
                        }                              
                        if(updateBeginCount - tupleCount > 0) {
                            buffer.begins.add(last.begin);
                            buffer.beginTupleIdx.add((int) (updateBeginCount - tupleCount));
                        }
                        bufferStartCount = tupleCount;
                    }
                    
                    while (!buffer.isFull()) {
                        updateBegin();
                        buffer.processTuple(input.readBytes(), input.readInt());
                        tupleCount++;
                    }
                } catch(EOFException e) {}
            }

            public final void updateNumber() throws IOException {
                if (updateNumberCount > tupleCount)
                    return;
                     
                last.number = input.readLong();
                updateNumberCount = tupleCount + input.readInt();
                                      
                buffer.processNumber(last.number);
            }
            public final void updateBegin() throws IOException {
                if (updateBeginCount > tupleCount)
                    return;
                     
                updateNumber();
                last.begin = input.readInt();
                updateBeginCount = tupleCount + input.readInt();
                                      
                buffer.processBegin(last.begin);
            }

            public void run() throws IOException {
                while (true) {
                    fill();
                    
                    if (buffer.isAtEnd())
                        break;
                    
                    buffer.copyUntil(null, processor);
                }      
                processor.close();
            }
            
            public void setProcessor(Step processor) throws IncompatibleProcessorException {  
                if (processor instanceof ShreddedProcessor) {
                    this.processor = new DuplicateEliminator((ShreddedProcessor) processor);
                } else if (processor instanceof NumberedExtent.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((NumberedExtent.Processor) processor));
                } else if (processor instanceof org.lemurproject.galago.tupleflow.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((org.lemurproject.galago.tupleflow.Processor<NumberedExtent>) processor));
                } else {
                    throw new IncompatibleProcessorException(processor.getClass().getName() + " is not supported by " + this.getClass().getName());                                                                       
                }
            }                                
            
            public Class<NumberedExtent> getOutputClass() {
                return NumberedExtent.class;
            }                
        }
        
        public static final class DuplicateEliminator implements ShreddedProcessor {
            public ShreddedProcessor processor;
            NumberedExtent last = new NumberedExtent();
            boolean numberProcess = true;
            boolean beginProcess = true;
                                           
            public DuplicateEliminator() {}
            public DuplicateEliminator(ShreddedProcessor processor) {
                this.processor = processor;
            }
            
            public void setShreddedProcessor(ShreddedProcessor processor) {
                this.processor = processor;
            }

            public void processNumber(long number) throws IOException {  
                if (numberProcess || CmpUtil.compare(number, last.number) != 0) {
                    last.number = number;
                    processor.processNumber(number);
            resetBegin();
                    numberProcess = false;
                }
            }
            public void processBegin(int begin) throws IOException {  
                if (beginProcess || CmpUtil.compare(begin, last.begin) != 0) {
                    last.begin = begin;
                    processor.processBegin(begin);
                    beginProcess = false;
                }
            }  
            
            public void resetNumber() {
                 numberProcess = true;
            resetBegin();
            }                                                
            public void resetBegin() {
                 beginProcess = true;
            }                                                
                               
            public void processTuple(byte[] extentName, int end) throws IOException {
                processor.processTuple(extentName, end);
            } 

            @Override
            public void close() throws IOException {
                processor.close();
            }                    
        }
        public static final class TupleUnshredder implements ShreddedProcessor {
            NumberedExtent last = new NumberedExtent();
            public org.lemurproject.galago.tupleflow.Processor<NumberedExtent> processor;                               
            
            public TupleUnshredder(NumberedExtent.Processor processor) {
                this.processor = processor;
            }         
            
            public TupleUnshredder(org.lemurproject.galago.tupleflow.Processor<NumberedExtent> processor) {
                this.processor = processor;
            }
            
            public NumberedExtent clone(NumberedExtent object) {
                NumberedExtent result = new NumberedExtent();
                if (object == null) return result;
                result.extentName = object.extentName; 
                result.number = object.number; 
                result.begin = object.begin; 
                result.end = object.end; 
                return result;
            }                 
            
            public void processNumber(long number) throws IOException {
                last.number = number;
            }   
                
            public void processBegin(int begin) throws IOException {
                last.begin = begin;
            }   
                
            
            public void processTuple(byte[] extentName, int end) throws IOException {
                last.extentName = extentName;
                last.end = end;
                processor.process(clone(last));
            }               

            @Override
            public void close() throws IOException {
                processor.close();
            }
        }     
        public static final class TupleShredder implements Processor {
            NumberedExtent last = null;
            public ShreddedProcessor processor;
            
            public TupleShredder(ShreddedProcessor processor) {
                this.processor = processor;
            }                              
            
            public NumberedExtent clone(NumberedExtent object) {
                NumberedExtent result = new NumberedExtent();
                if (object == null) return result;
                result.extentName = object.extentName; 
                result.number = object.number; 
                result.begin = object.begin; 
                result.end = object.end; 
                return result;
            }                 
            
            public void process(NumberedExtent object) throws IOException {                                                                                                                                                   
                boolean processAll = false;
                if(last == null || CmpUtil.compare(last.number, object.number) != 0 || processAll) { processor.processNumber(object.number); processAll = true; }
                if(last == null || CmpUtil.compare(last.begin, object.begin) != 0 || processAll) { processor.processBegin(object.begin); processAll = true; }
                processor.processTuple(object.extentName, object.end);                                         
                last = object;
            }
                          
            public Class<NumberedExtent> getInputClass() {
                return NumberedExtent.class;
            }

            @Override
            public void close() throws IOException {
                processor.close();
            }                     
        }
    } 
    public static final class ExtentNameNumberBeginOrder implements Order<NumberedExtent> {
        public int hash(NumberedExtent object) {
            int h = 0;
            h += CmpUtil.hash(object.extentName);
            h += CmpUtil.hash(object.number);
            h += CmpUtil.hash(object.begin);
            return h;
        } 
        public Comparator<NumberedExtent> greaterThan() {
            return new Comparator<NumberedExtent>() {
                public int compare(NumberedExtent one, NumberedExtent two) {
                    int result = 0;
                    do {
                        result = + CmpUtil.compare(one.extentName, two.extentName);
                        if(result != 0) break;
                        result = + CmpUtil.compare(one.number, two.number);
                        if(result != 0) break;
                        result = + CmpUtil.compare(one.begin, two.begin);
                        if(result != 0) break;
                    } while (false);
                    return -result;
                }
            };
        }     
        public Comparator<NumberedExtent> lessThan() {
            return new Comparator<NumberedExtent>() {
                public int compare(NumberedExtent one, NumberedExtent two) {
                    int result = 0;
                    do {
                        result = + CmpUtil.compare(one.extentName, two.extentName);
                        if(result != 0) break;
                        result = + CmpUtil.compare(one.number, two.number);
                        if(result != 0) break;
                        result = + CmpUtil.compare(one.begin, two.begin);
                        if(result != 0) break;
                    } while (false);
                    return result;
                }
            };
        }     
        public TypeReader<NumberedExtent> orderedReader(ArrayInput _input) {
            return new ShreddedReader(_input);
        }    

        public TypeReader<NumberedExtent> orderedReader(ArrayInput _input, int bufferSize) {
            return new ShreddedReader(_input, bufferSize);
        }    
        public OrderedWriter<NumberedExtent> orderedWriter(ArrayOutput _output) {
            ShreddedWriter w = new ShreddedWriter(_output);
            return new OrderedWriterClass(w); 
        }                                    
        public static final class OrderedWriterClass extends OrderedWriter< NumberedExtent > {
            NumberedExtent last = null;
            ShreddedWriter shreddedWriter = null; 
            
            public OrderedWriterClass(ShreddedWriter s) {
                this.shreddedWriter = s;
            }
            
            public void process(NumberedExtent object) throws IOException {
               boolean processAll = false;
               if (processAll || last == null || 0 != CmpUtil.compare(object.extentName, last.extentName)) { processAll = true; shreddedWriter.processExtentName(object.extentName); }
               if (processAll || last == null || 0 != CmpUtil.compare(object.number, last.number)) { processAll = true; shreddedWriter.processNumber(object.number); }
               if (processAll || last == null || 0 != CmpUtil.compare(object.begin, last.begin)) { processAll = true; shreddedWriter.processBegin(object.begin); }
               shreddedWriter.processTuple(object.end);
               last = object;
            }           

            @Override
            public void close() throws IOException {
                shreddedWriter.close();
            }
            
            public Class<NumberedExtent> getInputClass() {
                return NumberedExtent.class;
            }
        } 
        public ReaderSource<NumberedExtent> orderedCombiner(Collection<TypeReader<NumberedExtent>> readers, boolean closeOnExit) {
            ArrayList<ShreddedReader> shreddedReaders = new ArrayList<ShreddedReader>();
            
            for (TypeReader<NumberedExtent> reader : readers) {
                shreddedReaders.add((ShreddedReader)reader);
            }
            
            return new ShreddedCombiner(shreddedReaders, closeOnExit);
        }                  
        public NumberedExtent clone(NumberedExtent object) {
            NumberedExtent result = new NumberedExtent();
            if (object == null) return result;
            result.extentName = object.extentName; 
            result.number = object.number; 
            result.begin = object.begin; 
            result.end = object.end; 
            return result;
        }                 
        public Class<NumberedExtent> getOrderedClass() {
            return NumberedExtent.class;
        }                           
        public String[] getOrderSpec() {
            return new String[] {"+extentName", "+number", "+begin"};
        }

        public static String[] getSpec() {
            return new String[] {"+extentName", "+number", "+begin"};
        }
        public static String getSpecString() {
            return "+extentName +number +begin";
        }
                           
        public interface ShreddedProcessor extends Step, Closeable {
            public void processExtentName(byte[] extentName) throws IOException;
            public void processNumber(long number) throws IOException;
            public void processBegin(int begin) throws IOException;
            public void processTuple(int end) throws IOException;
        } 

        public static final class ShreddedWriter implements ShreddedProcessor {
            ArrayOutput output;
            ShreddedBuffer buffer = new ShreddedBuffer();
            byte[] lastExtentName;
            long lastNumber;
            int lastBegin;
            boolean lastFlush = false;
            
            public ShreddedWriter(ArrayOutput output) {
                this.output = output;
            }                        

            @Override
            public void close() throws IOException {
                flush();
            }
            
            public void processExtentName(byte[] extentName) {
                lastExtentName = extentName;
                buffer.processExtentName(extentName);
            }
            public void processNumber(long number) {
                lastNumber = number;
                buffer.processNumber(number);
            }
            public void processBegin(int begin) {
                lastBegin = begin;
                buffer.processBegin(begin);
            }
            public final void processTuple(int end) throws IOException {
                if (lastFlush) {
                    if(buffer.extentNames.size() == 0) buffer.processExtentName(lastExtentName);
                    if(buffer.numbers.size() == 0) buffer.processNumber(lastNumber);
                    if(buffer.begins.size() == 0) buffer.processBegin(lastBegin);
                    lastFlush = false;
                }
                buffer.processTuple(end);
                if (buffer.isFull())
                    flush();
            }
            public final void flushTuples(int pauseIndex) throws IOException {
                
                while (buffer.getReadIndex() < pauseIndex) {
                           
                    output.writeInt(buffer.getEnd());
                    buffer.incrementTuple();
                }
            }  
            public final void flushExtentName(int pauseIndex) throws IOException {
                while (buffer.getReadIndex() < pauseIndex) {
                    int nextPause = buffer.getExtentNameEndIndex();
                    int count = nextPause - buffer.getReadIndex();
                    
                    output.writeBytes(buffer.getExtentName());
                    output.writeInt(count);
                    buffer.incrementExtentName();
                      
                    flushNumber(nextPause);
                    assert nextPause == buffer.getReadIndex();
                }
            }
            public final void flushNumber(int pauseIndex) throws IOException {
                while (buffer.getReadIndex() < pauseIndex) {
                    int nextPause = buffer.getNumberEndIndex();
                    int count = nextPause - buffer.getReadIndex();
                    
                    output.writeLong(buffer.getNumber());
                    output.writeInt(count);
                    buffer.incrementNumber();
                      
                    flushBegin(nextPause);
                    assert nextPause == buffer.getReadIndex();
                }
            }
            public final void flushBegin(int pauseIndex) throws IOException {
                while (buffer.getReadIndex() < pauseIndex) {
                    int nextPause = buffer.getBeginEndIndex();
                    int count = nextPause - buffer.getReadIndex();
                    
                    output.writeInt(buffer.getBegin());
                    output.writeInt(count);
                    buffer.incrementBegin();
                      
                    flushTuples(nextPause);
                    assert nextPause == buffer.getReadIndex();
                }
            }
            public void flush() throws IOException { 
                flushExtentName(buffer.getWriteIndex());
                buffer.reset(); 
                lastFlush = true;
            }                           
        }
        public static final class ShreddedBuffer {
            ArrayList<byte[]> extentNames = new ArrayList<byte[]>();
            TLongArrayList numbers = new TLongArrayList();
            TIntArrayList begins = new TIntArrayList();
            TIntArrayList extentNameTupleIdx = new TIntArrayList();
            TIntArrayList numberTupleIdx = new TIntArrayList();
            TIntArrayList beginTupleIdx = new TIntArrayList();
            int extentNameReadIdx = 0;
            int numberReadIdx = 0;
            int beginReadIdx = 0;
                            
            int[] ends;
            int writeTupleIndex = 0;
            int readTupleIndex = 0;
            int batchSize;

            public ShreddedBuffer(int batchSize) {
                this.batchSize = batchSize;

                ends = new int[batchSize];
            }                              

            public ShreddedBuffer() {    
                this(10000);
            }                                                                                                                    
            
            public void processExtentName(byte[] extentName) {
                extentNames.add(extentName);
                extentNameTupleIdx.add(writeTupleIndex);
            }                                      
            public void processNumber(long number) {
                numbers.add(number);
                numberTupleIdx.add(writeTupleIndex);
            }                                      
            public void processBegin(int begin) {
                begins.add(begin);
                beginTupleIdx.add(writeTupleIndex);
            }                                      
            public void processTuple(int end) {
                assert extentNames.size() > 0;
                assert numbers.size() > 0;
                assert begins.size() > 0;
                ends[writeTupleIndex] = end;
                writeTupleIndex++;
            }
            public void resetData() {
                extentNames.clear();
                numbers.clear();
                begins.clear();
                extentNameTupleIdx.clear();
                numberTupleIdx.clear();
                beginTupleIdx.clear();
                writeTupleIndex = 0;
            }                  
                                 
            public void resetRead() {
                readTupleIndex = 0;
                extentNameReadIdx = 0;
                numberReadIdx = 0;
                beginReadIdx = 0;
            } 

            public void reset() {
                resetData();
                resetRead();
            } 
            public boolean isFull() {
                return writeTupleIndex >= batchSize;
            }

            public boolean isEmpty() {
                return writeTupleIndex == 0;
            }                          

            public boolean isAtEnd() {
                return readTupleIndex >= writeTupleIndex;
            }           
            public void incrementExtentName() {
                extentNameReadIdx++;  
            }                                                                                              

            public void autoIncrementExtentName() {
                while (readTupleIndex >= getExtentNameEndIndex() && readTupleIndex < writeTupleIndex)
                    extentNameReadIdx++;
            }                 
            public void incrementNumber() {
                numberReadIdx++;  
            }                                                                                              

            public void autoIncrementNumber() {
                while (readTupleIndex >= getNumberEndIndex() && readTupleIndex < writeTupleIndex)
                    numberReadIdx++;
            }                 
            public void incrementBegin() {
                beginReadIdx++;  
            }                                                                                              

            public void autoIncrementBegin() {
                while (readTupleIndex >= getBeginEndIndex() && readTupleIndex < writeTupleIndex)
                    beginReadIdx++;
            }                 
            public void incrementTuple() {
                readTupleIndex++;
            }                    
            public int getExtentNameEndIndex() {
                if ((extentNameReadIdx+1) >= extentNameTupleIdx.size())
                    return writeTupleIndex;
                return extentNameTupleIdx.get(extentNameReadIdx+1);
            }

            public int getNumberEndIndex() {
                if ((numberReadIdx+1) >= numberTupleIdx.size())
                    return writeTupleIndex;
                return numberTupleIdx.get(numberReadIdx+1);
            }

            public int getBeginEndIndex() {
                if ((beginReadIdx+1) >= beginTupleIdx.size())
                    return writeTupleIndex;
                return beginTupleIdx.get(beginReadIdx+1);
            }
            public int getReadIndex() {
                return readTupleIndex;
            }   

            public int getWriteIndex() {
                return writeTupleIndex;
            } 
            public byte[] getExtentName() {
                assert readTupleIndex < writeTupleIndex;
                assert extentNameReadIdx < extentNames.size();
                
                return extentNames.get(extentNameReadIdx);
            }
            public long getNumber() {
                assert readTupleIndex < writeTupleIndex;
                assert numberReadIdx < numbers.size();
                
                return numbers.get(numberReadIdx);
            }
            public int getBegin() {
                assert readTupleIndex < writeTupleIndex;
                assert beginReadIdx < begins.size();
                
                return begins.get(beginReadIdx);
            }
            public int getEnd() {
                assert readTupleIndex < writeTupleIndex;
                return ends[readTupleIndex];
            }                                         
            public void copyTuples(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                   output.processTuple(getEnd());
                   incrementTuple();
                }
            }                                                                           
            public void copyUntilIndexExtentName(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                    output.processExtentName(getExtentName());
                    assert getExtentNameEndIndex() <= endIndex;
                    copyUntilIndexNumber(getExtentNameEndIndex(), output);
                    incrementExtentName();
                }
            } 
            public void copyUntilIndexNumber(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                    output.processNumber(getNumber());
                    assert getNumberEndIndex() <= endIndex;
                    copyUntilIndexBegin(getNumberEndIndex(), output);
                    incrementNumber();
                }
            } 
            public void copyUntilIndexBegin(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                    output.processBegin(getBegin());
                    assert getBeginEndIndex() <= endIndex;
                    copyTuples(getBeginEndIndex(), output);
                    incrementBegin();
                }
            }  
            public void copyUntilExtentName(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                while (!isAtEnd()) {
                    if (other != null) {   
                        assert !other.isAtEnd();
                        int c = + CmpUtil.compare(getExtentName(), other.getExtentName());
                    
                        if (c > 0) {
                            break;   
                        }
                        
                        output.processExtentName(getExtentName());
                                      
                        if (c < 0) {
                            copyUntilIndexNumber(getExtentNameEndIndex(), output);
                        } else if (c == 0) {
                            copyUntilNumber(other, output);
                            autoIncrementExtentName();
                            break;
                        }
                    } else {
                        output.processExtentName(getExtentName());
                        copyUntilIndexNumber(getExtentNameEndIndex(), output);
                    }
                    incrementExtentName();  
                    
               
                }
            }
            public void copyUntilNumber(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                while (!isAtEnd()) {
                    if (other != null) {   
                        assert !other.isAtEnd();
                        int c = + CmpUtil.compare(getNumber(), other.getNumber());
                    
                        if (c > 0) {
                            break;   
                        }
                        
                        output.processNumber(getNumber());
                                      
                        if (c < 0) {
                            copyUntilIndexBegin(getNumberEndIndex(), output);
                        } else if (c == 0) {
                            copyUntilBegin(other, output);
                            autoIncrementNumber();
                            break;
                        }
                    } else {
                        output.processNumber(getNumber());
                        copyUntilIndexBegin(getNumberEndIndex(), output);
                    }
                    incrementNumber();  
                    
                    if (getExtentNameEndIndex() <= readTupleIndex)
                        break;   
                }
            }
            public void copyUntilBegin(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                while (!isAtEnd()) {
                    if (other != null) {   
                        assert !other.isAtEnd();
                        int c = + CmpUtil.compare(getBegin(), other.getBegin());
                    
                        if (c > 0) {
                            break;   
                        }
                        
                        output.processBegin(getBegin());
                                      
                        copyTuples(getBeginEndIndex(), output);
                    } else {
                        output.processBegin(getBegin());
                        copyTuples(getBeginEndIndex(), output);
                    }
                    incrementBegin();  
                    
                    if (getNumberEndIndex() <= readTupleIndex)
                        break;   
                }
            }
            public void copyUntil(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                copyUntilExtentName(other, output);
            }
            
        }                         
        public static final class ShreddedCombiner implements ReaderSource<NumberedExtent>, ShreddedSource {
            public ShreddedProcessor processor;
            Collection<ShreddedReader> readers;       
            boolean closeOnExit = false;
            boolean uninitialized = true;
            PriorityQueue<ShreddedReader> queue = new PriorityQueue<ShreddedReader>();
            
            public ShreddedCombiner(Collection<ShreddedReader> readers, boolean closeOnExit) {
                this.readers = readers;                                                       
                this.closeOnExit = closeOnExit;
            }
                                  
            public void setProcessor(Step processor) throws IncompatibleProcessorException {  
                if (processor instanceof ShreddedProcessor) {
                    this.processor = new DuplicateEliminator((ShreddedProcessor) processor);
                } else if (processor instanceof NumberedExtent.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((NumberedExtent.Processor) processor));
                } else if (processor instanceof org.lemurproject.galago.tupleflow.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((org.lemurproject.galago.tupleflow.Processor<NumberedExtent>) processor));
                } else {
                    throw new IncompatibleProcessorException(processor.getClass().getName() + " is not supported by " + this.getClass().getName());                                                                       
                }
            }                                
            
            public Class<NumberedExtent> getOutputClass() {
                return NumberedExtent.class;
            }
            
            public void initialize() throws IOException {
                for (ShreddedReader reader : readers) {
                    reader.fill();                                        
                    
                    if (!reader.getBuffer().isAtEnd())
                        queue.add(reader);
                }   

                uninitialized = false;
            }

            public void run() throws IOException {
                initialize();
               
                while (queue.size() > 0) {
                    ShreddedReader top = queue.poll();
                    ShreddedReader next = null;
                    ShreddedBuffer nextBuffer = null; 
                    
                    assert !top.getBuffer().isAtEnd();
                                                  
                    if (queue.size() > 0) {
                        next = queue.peek();
                        nextBuffer = next.getBuffer();
                        assert !nextBuffer.isAtEnd();
                    }
                    
                    top.getBuffer().copyUntil(nextBuffer, processor);
                    if (top.getBuffer().isAtEnd())
                        top.fill();                 
                        
                    if (!top.getBuffer().isAtEnd())
                        queue.add(top);
                }              
                
                if (closeOnExit)
                    processor.close();
            }

            public NumberedExtent read() throws IOException {
                if (uninitialized)
                    initialize();

                NumberedExtent result = null;

                while (queue.size() > 0) {
                    ShreddedReader top = queue.poll();
                    result = top.read();

                    if (result != null) {
                        if (top.getBuffer().isAtEnd())
                            top.fill();

                        queue.offer(top);
                        break;
                    } 
                }

                return result;
            }
        } 
        public static final class ShreddedReader implements Step, Comparable<ShreddedReader>, TypeReader<NumberedExtent>, ShreddedSource {
            public ShreddedProcessor processor;
            ShreddedBuffer buffer;
            NumberedExtent last = new NumberedExtent();         
            long updateExtentNameCount = -1;
            long updateNumberCount = -1;
            long updateBeginCount = -1;
            long tupleCount = 0;
            long bufferStartCount = 0;  
            ArrayInput input;
            
            public ShreddedReader(ArrayInput input) {
                this.input = input; 
                this.buffer = new ShreddedBuffer();
            }                               
            
            public ShreddedReader(ArrayInput input, int bufferSize) { 
                this.input = input;
                this.buffer = new ShreddedBuffer(bufferSize);
            }
                 
            public final int compareTo(ShreddedReader other) {
                ShreddedBuffer otherBuffer = other.getBuffer();
                
                if (buffer.isAtEnd() && otherBuffer.isAtEnd()) {
                    return 0;                 
                } else if (buffer.isAtEnd()) {
                    return -1;
                } else if (otherBuffer.isAtEnd()) {
                    return 1;
                }
                                   
                int result = 0;
                do {
                    result = + CmpUtil.compare(buffer.getExtentName(), otherBuffer.getExtentName());
                    if(result != 0) break;
                    result = + CmpUtil.compare(buffer.getNumber(), otherBuffer.getNumber());
                    if(result != 0) break;
                    result = + CmpUtil.compare(buffer.getBegin(), otherBuffer.getBegin());
                    if(result != 0) break;
                } while (false);                                             
                
                return result;
            }
            
            public final ShreddedBuffer getBuffer() {
                return buffer;
            }                
            
            public final NumberedExtent read() throws IOException {
                if (buffer.isAtEnd()) {
                    fill();             
                
                    if (buffer.isAtEnd()) {
                        return null;
                    }
                }
                      
                assert !buffer.isAtEnd();
                NumberedExtent result = new NumberedExtent();
                
                result.extentName = buffer.getExtentName();
                result.number = buffer.getNumber();
                result.begin = buffer.getBegin();
                result.end = buffer.getEnd();
                
                buffer.incrementTuple();
                buffer.autoIncrementExtentName();
                buffer.autoIncrementNumber();
                buffer.autoIncrementBegin();
                
                return result;
            }           
            
            public final void fill() throws IOException {
                try {   
                    buffer.reset();
                    
                    if (tupleCount != 0) {
                                                      
                        if(updateExtentNameCount - tupleCount > 0) {
                            buffer.extentNames.add(last.extentName);
                            buffer.extentNameTupleIdx.add((int) (updateExtentNameCount - tupleCount));
                        }                              
                        if(updateNumberCount - tupleCount > 0) {
                            buffer.numbers.add(last.number);
                            buffer.numberTupleIdx.add((int) (updateNumberCount - tupleCount));
                        }                              
                        if(updateBeginCount - tupleCount > 0) {
                            buffer.begins.add(last.begin);
                            buffer.beginTupleIdx.add((int) (updateBeginCount - tupleCount));
                        }
                        bufferStartCount = tupleCount;
                    }
                    
                    while (!buffer.isFull()) {
                        updateBegin();
                        buffer.processTuple(input.readInt());
                        tupleCount++;
                    }
                } catch(EOFException e) {}
            }

            public final void updateExtentName() throws IOException {
                if (updateExtentNameCount > tupleCount)
                    return;
                     
                last.extentName = input.readBytes();
                updateExtentNameCount = tupleCount + input.readInt();
                                      
                buffer.processExtentName(last.extentName);
            }
            public final void updateNumber() throws IOException {
                if (updateNumberCount > tupleCount)
                    return;
                     
                updateExtentName();
                last.number = input.readLong();
                updateNumberCount = tupleCount + input.readInt();
                                      
                buffer.processNumber(last.number);
            }
            public final void updateBegin() throws IOException {
                if (updateBeginCount > tupleCount)
                    return;
                     
                updateNumber();
                last.begin = input.readInt();
                updateBeginCount = tupleCount + input.readInt();
                                      
                buffer.processBegin(last.begin);
            }

            public void run() throws IOException {
                while (true) {
                    fill();
                    
                    if (buffer.isAtEnd())
                        break;
                    
                    buffer.copyUntil(null, processor);
                }      
                processor.close();
            }
            
            public void setProcessor(Step processor) throws IncompatibleProcessorException {  
                if (processor instanceof ShreddedProcessor) {
                    this.processor = new DuplicateEliminator((ShreddedProcessor) processor);
                } else if (processor instanceof NumberedExtent.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((NumberedExtent.Processor) processor));
                } else if (processor instanceof org.lemurproject.galago.tupleflow.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((org.lemurproject.galago.tupleflow.Processor<NumberedExtent>) processor));
                } else {
                    throw new IncompatibleProcessorException(processor.getClass().getName() + " is not supported by " + this.getClass().getName());                                                                       
                }
            }                                
            
            public Class<NumberedExtent> getOutputClass() {
                return NumberedExtent.class;
            }                
        }
        
        public static final class DuplicateEliminator implements ShreddedProcessor {
            public ShreddedProcessor processor;
            NumberedExtent last = new NumberedExtent();
            boolean extentNameProcess = true;
            boolean numberProcess = true;
            boolean beginProcess = true;
                                           
            public DuplicateEliminator() {}
            public DuplicateEliminator(ShreddedProcessor processor) {
                this.processor = processor;
            }
            
            public void setShreddedProcessor(ShreddedProcessor processor) {
                this.processor = processor;
            }

            public void processExtentName(byte[] extentName) throws IOException {  
                if (extentNameProcess || CmpUtil.compare(extentName, last.extentName) != 0) {
                    last.extentName = extentName;
                    processor.processExtentName(extentName);
            resetNumber();
                    extentNameProcess = false;
                }
            }
            public void processNumber(long number) throws IOException {  
                if (numberProcess || CmpUtil.compare(number, last.number) != 0) {
                    last.number = number;
                    processor.processNumber(number);
            resetBegin();
                    numberProcess = false;
                }
            }
            public void processBegin(int begin) throws IOException {  
                if (beginProcess || CmpUtil.compare(begin, last.begin) != 0) {
                    last.begin = begin;
                    processor.processBegin(begin);
                    beginProcess = false;
                }
            }  
            
            public void resetExtentName() {
                 extentNameProcess = true;
            resetNumber();
            }                                                
            public void resetNumber() {
                 numberProcess = true;
            resetBegin();
            }                                                
            public void resetBegin() {
                 beginProcess = true;
            }                                                
                               
            public void processTuple(int end) throws IOException {
                processor.processTuple(end);
            } 

            @Override
            public void close() throws IOException {
                processor.close();
            }                    
        }
        public static final class TupleUnshredder implements ShreddedProcessor {
            NumberedExtent last = new NumberedExtent();
            public org.lemurproject.galago.tupleflow.Processor<NumberedExtent> processor;                               
            
            public TupleUnshredder(NumberedExtent.Processor processor) {
                this.processor = processor;
            }         
            
            public TupleUnshredder(org.lemurproject.galago.tupleflow.Processor<NumberedExtent> processor) {
                this.processor = processor;
            }
            
            public NumberedExtent clone(NumberedExtent object) {
                NumberedExtent result = new NumberedExtent();
                if (object == null) return result;
                result.extentName = object.extentName; 
                result.number = object.number; 
                result.begin = object.begin; 
                result.end = object.end; 
                return result;
            }                 
            
            public void processExtentName(byte[] extentName) throws IOException {
                last.extentName = extentName;
            }   
                
            public void processNumber(long number) throws IOException {
                last.number = number;
            }   
                
            public void processBegin(int begin) throws IOException {
                last.begin = begin;
            }   
                
            
            public void processTuple(int end) throws IOException {
                last.end = end;
                processor.process(clone(last));
            }               

            @Override
            public void close() throws IOException {
                processor.close();
            }
        }     
        public static final class TupleShredder implements Processor {
            NumberedExtent last = null;
            public ShreddedProcessor processor;
            
            public TupleShredder(ShreddedProcessor processor) {
                this.processor = processor;
            }                              
            
            public NumberedExtent clone(NumberedExtent object) {
                NumberedExtent result = new NumberedExtent();
                if (object == null) return result;
                result.extentName = object.extentName; 
                result.number = object.number; 
                result.begin = object.begin; 
                result.end = object.end; 
                return result;
            }                 
            
            public void process(NumberedExtent object) throws IOException {                                                                                                                                                   
                boolean processAll = false;
                if(last == null || CmpUtil.compare(last.extentName, object.extentName) != 0 || processAll) { processor.processExtentName(object.extentName); processAll = true; }
                if(last == null || CmpUtil.compare(last.number, object.number) != 0 || processAll) { processor.processNumber(object.number); processAll = true; }
                if(last == null || CmpUtil.compare(last.begin, object.begin) != 0 || processAll) { processor.processBegin(object.begin); processAll = true; }
                processor.processTuple(object.end);                                         
                last = object;
            }
                          
            public Class<NumberedExtent> getInputClass() {
                return NumberedExtent.class;
            }

            @Override
            public void close() throws IOException {
                processor.close();
            }                     
        }
    } 
    public static final class ExtentNameOrder implements Order<NumberedExtent> {
        public int hash(NumberedExtent object) {
            int h = 0;
            h += CmpUtil.hash(object.extentName);
            return h;
        } 
        public Comparator<NumberedExtent> greaterThan() {
            return new Comparator<NumberedExtent>() {
                public int compare(NumberedExtent one, NumberedExtent two) {
                    int result = 0;
                    do {
                        result = + CmpUtil.compare(one.extentName, two.extentName);
                        if(result != 0) break;
                    } while (false);
                    return -result;
                }
            };
        }     
        public Comparator<NumberedExtent> lessThan() {
            return new Comparator<NumberedExtent>() {
                public int compare(NumberedExtent one, NumberedExtent two) {
                    int result = 0;
                    do {
                        result = + CmpUtil.compare(one.extentName, two.extentName);
                        if(result != 0) break;
                    } while (false);
                    return result;
                }
            };
        }     
        public TypeReader<NumberedExtent> orderedReader(ArrayInput _input) {
            return new ShreddedReader(_input);
        }    

        public TypeReader<NumberedExtent> orderedReader(ArrayInput _input, int bufferSize) {
            return new ShreddedReader(_input, bufferSize);
        }    
        public OrderedWriter<NumberedExtent> orderedWriter(ArrayOutput _output) {
            ShreddedWriter w = new ShreddedWriter(_output);
            return new OrderedWriterClass(w); 
        }                                    
        public static final class OrderedWriterClass extends OrderedWriter< NumberedExtent > {
            NumberedExtent last = null;
            ShreddedWriter shreddedWriter = null; 
            
            public OrderedWriterClass(ShreddedWriter s) {
                this.shreddedWriter = s;
            }
            
            public void process(NumberedExtent object) throws IOException {
               boolean processAll = false;
               if (processAll || last == null || 0 != CmpUtil.compare(object.extentName, last.extentName)) { processAll = true; shreddedWriter.processExtentName(object.extentName); }
               shreddedWriter.processTuple(object.number, object.begin, object.end);
               last = object;
            }           

            @Override
            public void close() throws IOException {
                shreddedWriter.close();
            }
            
            public Class<NumberedExtent> getInputClass() {
                return NumberedExtent.class;
            }
        } 
        public ReaderSource<NumberedExtent> orderedCombiner(Collection<TypeReader<NumberedExtent>> readers, boolean closeOnExit) {
            ArrayList<ShreddedReader> shreddedReaders = new ArrayList<ShreddedReader>();
            
            for (TypeReader<NumberedExtent> reader : readers) {
                shreddedReaders.add((ShreddedReader)reader);
            }
            
            return new ShreddedCombiner(shreddedReaders, closeOnExit);
        }                  
        public NumberedExtent clone(NumberedExtent object) {
            NumberedExtent result = new NumberedExtent();
            if (object == null) return result;
            result.extentName = object.extentName; 
            result.number = object.number; 
            result.begin = object.begin; 
            result.end = object.end; 
            return result;
        }                 
        public Class<NumberedExtent> getOrderedClass() {
            return NumberedExtent.class;
        }                           
        public String[] getOrderSpec() {
            return new String[] {"+extentName"};
        }

        public static String[] getSpec() {
            return new String[] {"+extentName"};
        }
        public static String getSpecString() {
            return "+extentName";
        }
                           
        public interface ShreddedProcessor extends Step, Closeable {
            public void processExtentName(byte[] extentName) throws IOException;
            public void processTuple(long number, int begin, int end) throws IOException;
        } 

        public static final class ShreddedWriter implements ShreddedProcessor {
            ArrayOutput output;
            ShreddedBuffer buffer = new ShreddedBuffer();
            byte[] lastExtentName;
            boolean lastFlush = false;
            
            public ShreddedWriter(ArrayOutput output) {
                this.output = output;
            }                        

            @Override
            public void close() throws IOException {
                flush();
            }
            
            public void processExtentName(byte[] extentName) {
                lastExtentName = extentName;
                buffer.processExtentName(extentName);
            }
            public final void processTuple(long number, int begin, int end) throws IOException {
                if (lastFlush) {
                    if(buffer.extentNames.size() == 0) buffer.processExtentName(lastExtentName);
                    lastFlush = false;
                }
                buffer.processTuple(number, begin, end);
                if (buffer.isFull())
                    flush();
            }
            public final void flushTuples(int pauseIndex) throws IOException {
                
                while (buffer.getReadIndex() < pauseIndex) {
                           
                    output.writeLong(buffer.getNumber());
                    output.writeInt(buffer.getBegin());
                    output.writeInt(buffer.getEnd());
                    buffer.incrementTuple();
                }
            }  
            public final void flushExtentName(int pauseIndex) throws IOException {
                while (buffer.getReadIndex() < pauseIndex) {
                    int nextPause = buffer.getExtentNameEndIndex();
                    int count = nextPause - buffer.getReadIndex();
                    
                    output.writeBytes(buffer.getExtentName());
                    output.writeInt(count);
                    buffer.incrementExtentName();
                      
                    flushTuples(nextPause);
                    assert nextPause == buffer.getReadIndex();
                }
            }
            public void flush() throws IOException { 
                flushExtentName(buffer.getWriteIndex());
                buffer.reset(); 
                lastFlush = true;
            }                           
        }
        public static final class ShreddedBuffer {
            ArrayList<byte[]> extentNames = new ArrayList<byte[]>();
            TIntArrayList extentNameTupleIdx = new TIntArrayList();
            int extentNameReadIdx = 0;
                            
            long[] numbers;
            int[] begins;
            int[] ends;
            int writeTupleIndex = 0;
            int readTupleIndex = 0;
            int batchSize;

            public ShreddedBuffer(int batchSize) {
                this.batchSize = batchSize;

                numbers = new long[batchSize];
                begins = new int[batchSize];
                ends = new int[batchSize];
            }                              

            public ShreddedBuffer() {    
                this(10000);
            }                                                                                                                    
            
            public void processExtentName(byte[] extentName) {
                extentNames.add(extentName);
                extentNameTupleIdx.add(writeTupleIndex);
            }                                      
            public void processTuple(long number, int begin, int end) {
                assert extentNames.size() > 0;
                numbers[writeTupleIndex] = number;
                begins[writeTupleIndex] = begin;
                ends[writeTupleIndex] = end;
                writeTupleIndex++;
            }
            public void resetData() {
                extentNames.clear();
                extentNameTupleIdx.clear();
                writeTupleIndex = 0;
            }                  
                                 
            public void resetRead() {
                readTupleIndex = 0;
                extentNameReadIdx = 0;
            } 

            public void reset() {
                resetData();
                resetRead();
            } 
            public boolean isFull() {
                return writeTupleIndex >= batchSize;
            }

            public boolean isEmpty() {
                return writeTupleIndex == 0;
            }                          

            public boolean isAtEnd() {
                return readTupleIndex >= writeTupleIndex;
            }           
            public void incrementExtentName() {
                extentNameReadIdx++;  
            }                                                                                              

            public void autoIncrementExtentName() {
                while (readTupleIndex >= getExtentNameEndIndex() && readTupleIndex < writeTupleIndex)
                    extentNameReadIdx++;
            }                 
            public void incrementTuple() {
                readTupleIndex++;
            }                    
            public int getExtentNameEndIndex() {
                if ((extentNameReadIdx+1) >= extentNameTupleIdx.size())
                    return writeTupleIndex;
                return extentNameTupleIdx.get(extentNameReadIdx+1);
            }
            public int getReadIndex() {
                return readTupleIndex;
            }   

            public int getWriteIndex() {
                return writeTupleIndex;
            } 
            public byte[] getExtentName() {
                assert readTupleIndex < writeTupleIndex;
                assert extentNameReadIdx < extentNames.size();
                
                return extentNames.get(extentNameReadIdx);
            }
            public long getNumber() {
                assert readTupleIndex < writeTupleIndex;
                return numbers[readTupleIndex];
            }                                         
            public int getBegin() {
                assert readTupleIndex < writeTupleIndex;
                return begins[readTupleIndex];
            }                                         
            public int getEnd() {
                assert readTupleIndex < writeTupleIndex;
                return ends[readTupleIndex];
            }                                         
            public void copyTuples(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                   output.processTuple(getNumber(), getBegin(), getEnd());
                   incrementTuple();
                }
            }                                                                           
            public void copyUntilIndexExtentName(int endIndex, ShreddedProcessor output) throws IOException {
                while (getReadIndex() < endIndex) {
                    output.processExtentName(getExtentName());
                    assert getExtentNameEndIndex() <= endIndex;
                    copyTuples(getExtentNameEndIndex(), output);
                    incrementExtentName();
                }
            }  
            public void copyUntilExtentName(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                while (!isAtEnd()) {
                    if (other != null) {   
                        assert !other.isAtEnd();
                        int c = + CmpUtil.compare(getExtentName(), other.getExtentName());
                    
                        if (c > 0) {
                            break;   
                        }
                        
                        output.processExtentName(getExtentName());
                                      
                        copyTuples(getExtentNameEndIndex(), output);
                    } else {
                        output.processExtentName(getExtentName());
                        copyTuples(getExtentNameEndIndex(), output);
                    }
                    incrementExtentName();  
                    
               
                }
            }
            public void copyUntil(ShreddedBuffer other, ShreddedProcessor output) throws IOException {
                copyUntilExtentName(other, output);
            }
            
        }                         
        public static final class ShreddedCombiner implements ReaderSource<NumberedExtent>, ShreddedSource {
            public ShreddedProcessor processor;
            Collection<ShreddedReader> readers;       
            boolean closeOnExit = false;
            boolean uninitialized = true;
            PriorityQueue<ShreddedReader> queue = new PriorityQueue<ShreddedReader>();
            
            public ShreddedCombiner(Collection<ShreddedReader> readers, boolean closeOnExit) {
                this.readers = readers;                                                       
                this.closeOnExit = closeOnExit;
            }
                                  
            public void setProcessor(Step processor) throws IncompatibleProcessorException {  
                if (processor instanceof ShreddedProcessor) {
                    this.processor = new DuplicateEliminator((ShreddedProcessor) processor);
                } else if (processor instanceof NumberedExtent.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((NumberedExtent.Processor) processor));
                } else if (processor instanceof org.lemurproject.galago.tupleflow.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((org.lemurproject.galago.tupleflow.Processor<NumberedExtent>) processor));
                } else {
                    throw new IncompatibleProcessorException(processor.getClass().getName() + " is not supported by " + this.getClass().getName());                                                                       
                }
            }                                
            
            public Class<NumberedExtent> getOutputClass() {
                return NumberedExtent.class;
            }
            
            public void initialize() throws IOException {
                for (ShreddedReader reader : readers) {
                    reader.fill();                                        
                    
                    if (!reader.getBuffer().isAtEnd())
                        queue.add(reader);
                }   

                uninitialized = false;
            }

            public void run() throws IOException {
                initialize();
               
                while (queue.size() > 0) {
                    ShreddedReader top = queue.poll();
                    ShreddedReader next = null;
                    ShreddedBuffer nextBuffer = null; 
                    
                    assert !top.getBuffer().isAtEnd();
                                                  
                    if (queue.size() > 0) {
                        next = queue.peek();
                        nextBuffer = next.getBuffer();
                        assert !nextBuffer.isAtEnd();
                    }
                    
                    top.getBuffer().copyUntil(nextBuffer, processor);
                    if (top.getBuffer().isAtEnd())
                        top.fill();                 
                        
                    if (!top.getBuffer().isAtEnd())
                        queue.add(top);
                }              
                
                if (closeOnExit)
                    processor.close();
            }

            public NumberedExtent read() throws IOException {
                if (uninitialized)
                    initialize();

                NumberedExtent result = null;

                while (queue.size() > 0) {
                    ShreddedReader top = queue.poll();
                    result = top.read();

                    if (result != null) {
                        if (top.getBuffer().isAtEnd())
                            top.fill();

                        queue.offer(top);
                        break;
                    } 
                }

                return result;
            }
        } 
        public static final class ShreddedReader implements Step, Comparable<ShreddedReader>, TypeReader<NumberedExtent>, ShreddedSource {
            public ShreddedProcessor processor;
            ShreddedBuffer buffer;
            NumberedExtent last = new NumberedExtent();         
            long updateExtentNameCount = -1;
            long tupleCount = 0;
            long bufferStartCount = 0;  
            ArrayInput input;
            
            public ShreddedReader(ArrayInput input) {
                this.input = input; 
                this.buffer = new ShreddedBuffer();
            }                               
            
            public ShreddedReader(ArrayInput input, int bufferSize) { 
                this.input = input;
                this.buffer = new ShreddedBuffer(bufferSize);
            }
                 
            public final int compareTo(ShreddedReader other) {
                ShreddedBuffer otherBuffer = other.getBuffer();
                
                if (buffer.isAtEnd() && otherBuffer.isAtEnd()) {
                    return 0;                 
                } else if (buffer.isAtEnd()) {
                    return -1;
                } else if (otherBuffer.isAtEnd()) {
                    return 1;
                }
                                   
                int result = 0;
                do {
                    result = + CmpUtil.compare(buffer.getExtentName(), otherBuffer.getExtentName());
                    if(result != 0) break;
                } while (false);                                             
                
                return result;
            }
            
            public final ShreddedBuffer getBuffer() {
                return buffer;
            }                
            
            public final NumberedExtent read() throws IOException {
                if (buffer.isAtEnd()) {
                    fill();             
                
                    if (buffer.isAtEnd()) {
                        return null;
                    }
                }
                      
                assert !buffer.isAtEnd();
                NumberedExtent result = new NumberedExtent();
                
                result.extentName = buffer.getExtentName();
                result.number = buffer.getNumber();
                result.begin = buffer.getBegin();
                result.end = buffer.getEnd();
                
                buffer.incrementTuple();
                buffer.autoIncrementExtentName();
                
                return result;
            }           
            
            public final void fill() throws IOException {
                try {   
                    buffer.reset();
                    
                    if (tupleCount != 0) {
                                                      
                        if(updateExtentNameCount - tupleCount > 0) {
                            buffer.extentNames.add(last.extentName);
                            buffer.extentNameTupleIdx.add((int) (updateExtentNameCount - tupleCount));
                        }
                        bufferStartCount = tupleCount;
                    }
                    
                    while (!buffer.isFull()) {
                        updateExtentName();
                        buffer.processTuple(input.readLong(), input.readInt(), input.readInt());
                        tupleCount++;
                    }
                } catch(EOFException e) {}
            }

            public final void updateExtentName() throws IOException {
                if (updateExtentNameCount > tupleCount)
                    return;
                     
                last.extentName = input.readBytes();
                updateExtentNameCount = tupleCount + input.readInt();
                                      
                buffer.processExtentName(last.extentName);
            }

            public void run() throws IOException {
                while (true) {
                    fill();
                    
                    if (buffer.isAtEnd())
                        break;
                    
                    buffer.copyUntil(null, processor);
                }      
                processor.close();
            }
            
            public void setProcessor(Step processor) throws IncompatibleProcessorException {  
                if (processor instanceof ShreddedProcessor) {
                    this.processor = new DuplicateEliminator((ShreddedProcessor) processor);
                } else if (processor instanceof NumberedExtent.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((NumberedExtent.Processor) processor));
                } else if (processor instanceof org.lemurproject.galago.tupleflow.Processor) {
                    this.processor = new DuplicateEliminator(new TupleUnshredder((org.lemurproject.galago.tupleflow.Processor<NumberedExtent>) processor));
                } else {
                    throw new IncompatibleProcessorException(processor.getClass().getName() + " is not supported by " + this.getClass().getName());                                                                       
                }
            }                                
            
            public Class<NumberedExtent> getOutputClass() {
                return NumberedExtent.class;
            }                
        }
        
        public static final class DuplicateEliminator implements ShreddedProcessor {
            public ShreddedProcessor processor;
            NumberedExtent last = new NumberedExtent();
            boolean extentNameProcess = true;
                                           
            public DuplicateEliminator() {}
            public DuplicateEliminator(ShreddedProcessor processor) {
                this.processor = processor;
            }
            
            public void setShreddedProcessor(ShreddedProcessor processor) {
                this.processor = processor;
            }

            public void processExtentName(byte[] extentName) throws IOException {  
                if (extentNameProcess || CmpUtil.compare(extentName, last.extentName) != 0) {
                    last.extentName = extentName;
                    processor.processExtentName(extentName);
                    extentNameProcess = false;
                }
            }  
            
            public void resetExtentName() {
                 extentNameProcess = true;
            }                                                
                               
            public void processTuple(long number, int begin, int end) throws IOException {
                processor.processTuple(number, begin, end);
            } 

            @Override
            public void close() throws IOException {
                processor.close();
            }                    
        }
        public static final class TupleUnshredder implements ShreddedProcessor {
            NumberedExtent last = new NumberedExtent();
            public org.lemurproject.galago.tupleflow.Processor<NumberedExtent> processor;                               
            
            public TupleUnshredder(NumberedExtent.Processor processor) {
                this.processor = processor;
            }         
            
            public TupleUnshredder(org.lemurproject.galago.tupleflow.Processor<NumberedExtent> processor) {
                this.processor = processor;
            }
            
            public NumberedExtent clone(NumberedExtent object) {
                NumberedExtent result = new NumberedExtent();
                if (object == null) return result;
                result.extentName = object.extentName; 
                result.number = object.number; 
                result.begin = object.begin; 
                result.end = object.end; 
                return result;
            }                 
            
            public void processExtentName(byte[] extentName) throws IOException {
                last.extentName = extentName;
            }   
                
            
            public void processTuple(long number, int begin, int end) throws IOException {
                last.number = number;
                last.begin = begin;
                last.end = end;
                processor.process(clone(last));
            }               

            @Override
            public void close() throws IOException {
                processor.close();
            }
        }     
        public static final class TupleShredder implements Processor {
            NumberedExtent last = null;
            public ShreddedProcessor processor;
            
            public TupleShredder(ShreddedProcessor processor) {
                this.processor = processor;
            }                              
            
            public NumberedExtent clone(NumberedExtent object) {
                NumberedExtent result = new NumberedExtent();
                if (object == null) return result;
                result.extentName = object.extentName; 
                result.number = object.number; 
                result.begin = object.begin; 
                result.end = object.end; 
                return result;
            }                 
            
            public void process(NumberedExtent object) throws IOException {                                                                                                                                                   
                boolean processAll = false;
                if(last == null || CmpUtil.compare(last.extentName, object.extentName) != 0 || processAll) { processor.processExtentName(object.extentName); processAll = true; }
                processor.processTuple(object.number, object.begin, object.end);                                         
                last = object;
            }
                          
            public Class<NumberedExtent> getInputClass() {
                return NumberedExtent.class;
            }

            @Override
            public void close() throws IOException {
                processor.close();
            }                     
        }
    } 
}    