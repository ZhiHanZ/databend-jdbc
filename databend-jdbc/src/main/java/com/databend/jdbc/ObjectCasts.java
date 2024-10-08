package com.databend.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

import static java.lang.Byte.parseByte;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Short.parseShort;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;

final class ObjectCasts {
    private ObjectCasts() {
    }

    public static boolean castToBoolean(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return (Boolean) x;
        }
        try {
            if (x instanceof Number) {
                return ((Number) x).intValue() != 0;
            }
            if (x instanceof String) {
                switch (((String) x).toLowerCase(ENGLISH)) {
                    case "0":
                    case "false":
                        return false;
                    case "1":
                    case "true":
                        return true;
                }
                throw new IllegalArgumentException("Invalid boolean value: " + x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static byte castToByte(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return (byte) (((Boolean) x) ? 1 : 0);
        }
        try {
            if (x instanceof Number) {
                return ((Number) x).byteValue();
            }
            if (x instanceof String) {
                return parseByte((String) x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static short castToShort(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return (short) (((Boolean) x) ? 1 : 0);
        }
        try {
            if (x instanceof Number) {
                return ((Number) x).shortValue();
            }
            if (x instanceof String) {
                return parseShort((String) x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static int castToInt(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return (((Boolean) x) ? 1 : 0);
        }
        try {
            if (x instanceof Number) {
                return ((Number) x).intValue();
            }
            if (x instanceof String) {
                return parseInt((String) x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static long castToLong(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return (((Boolean) x) ? 1 : 0);
        }
        try {
            if (x instanceof Number) {
                return ((Number) x).longValue();
            }
            if (x instanceof String) {
                return parseLong((String) x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static float castToFloat(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return (((Boolean) x) ? 1 : 0);
        }
        try {
            if (x instanceof Number) {
                return ((Number) x).floatValue();
            }
            if (x instanceof String) {
                return parseFloat((String) x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static double castToDouble(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return (((Boolean) x) ? 1 : 0);
        }
        try {
            if (x instanceof Number) {
                return ((Number) x).doubleValue();
            }
            if (x instanceof String) {
                return parseDouble((String) x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static BigDecimal castToBigDecimal(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof Boolean) {
            return BigDecimal.valueOf(((Boolean) x) ? 1 : 0);
        }
        if (x instanceof BigInteger) {
            return new BigDecimal((BigInteger) x);
        }
        if (x instanceof BigDecimal) {
            return (BigDecimal) x;
        }
        try {
            if ((x instanceof Byte) || (x instanceof Short) || (x instanceof Integer) || (x instanceof Long)) {
                return BigDecimal.valueOf(((Number) x).longValue());
            }
            if ((x instanceof Float) || (x instanceof Double)) {
                return BigDecimal.valueOf(((Number) x).doubleValue());
            }
            if (x instanceof String) {
                return new BigDecimal((String) x);
            }
        } catch (RuntimeException e) {
            throw invalidConversion(x, targetSqlType, e);
        }
        throw invalidConversion(x, targetSqlType);
    }

    public static byte[] castToBinary(Object x, int targetSqlType)
            throws SQLException {
        if (x instanceof byte[]) {
            return (byte[]) x;
        }
        if (x instanceof String) {
            return ((String) x).getBytes(UTF_8);
        }
        throw invalidConversion(x, targetSqlType);
    }

    private static SQLException invalidConversion(Object x, int sqlType) {
        return invalidConversion(x, sqlType, null);
    }

    private static SQLException invalidConversion(Object x, int sqlType, Exception e) {
        return new SQLException(format("Cannot convert instance of %s to SQL type %s", x.getClass().getName(), sqlType), e);
    }

    static SQLException invalidConversion(Object x, String toType) {
        return new SQLException(format("Cannot convert instance of %s to %s", x.getClass().getName(), toType));
    }
}
