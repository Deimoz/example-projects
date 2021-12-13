cd "$(dirname "$0")" || exit

javaAdvLib=../../../../../../../java-advanced-2021
implModule="$javaAdvLib"/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor

javadoc -private -link https://docs.oracle.com/en/java/javase/11/docs/api/ Implementor.java "$implModule/Impler.java" "$implModule/JarImpler.java" "$implModule/ImplerException.java"