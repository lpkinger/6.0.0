package com.uas.erp.model.excel;

import java.io.IOException;
import java.util.Objects;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.uas.erp.model.excel.Triple.TripleSerializer;
@JsonSerialize(using=TripleSerializer.class)
public class Triple<T1,T2,T3> {
    
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    
    public Triple(T1 t1, T2 t2, T3 t3) {
        super();
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public T3 getT3() {
        return t3;
    }
    
    public int hashCode(){
        return Objects.hash(t1,t2,t3);
    }
    
    public boolean equals(Object o){
        if(o == this) return true;
        if(!(o instanceof Triple))return false;
        Triple<?,?,?> other = (Triple<?,?,?>)o;
        return Objects.equals(t1, other.t1) && Objects.equals(t2, other.t2) && Objects.equals(t3, other.t3);
    }
    
    
    public static class TripleSerializer extends JsonSerializer<Triple<?,?,?>> {

        @Override
        public void serialize(Triple<?,?,?> triple, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartArray();
            jgen.writeObject(triple.t1);
            jgen.writeObject(triple.t2);
            jgen.writeObject(triple.t3);
            jgen.writeEndArray();
        }
    }

}
