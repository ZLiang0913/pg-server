package com.zliang.pg.protocol.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class PgType {

    private int oid;
    private String name;
    private int arrayType;
    private int elementType;
    private int byteLength;
    private Type type;
    private Category category;

    @Getter
    public enum Type {
        Base('b'),
        Composite('c'),
        Domain('d'),
        Enum('e'),
        Pseudo('p'),
        Range('r'),
        Multirange('m');

        private char id;

        Type(char id) {
            this.id = id;
        }
    }

    @Getter
    public enum Category {
        Array('A'),
        Boolean('B'),
        Composite('C'),
        DateTime('D'),
        Enum('E'),
        Geometric('G'),
        NetworkAddress('I'),
        Numeric('N'),
        Pseudo('P'),
        Range('R'),
        String('S'),
        Timespan('T'),
        UserDefined('U'),
        BitString('V'),
        Unknown('X');

        private char id;

        Category(char id) {
            this.id = id;
        }

    }


}
