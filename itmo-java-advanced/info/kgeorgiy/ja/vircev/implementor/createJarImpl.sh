cd "$(dirname "$0")" || exit

javaAdvLib=../../../../../../../
artif=java-advanced-2021/artifacts/info.kgeorgiy.java.advanced.implementor.jar
implLib="$javaAdvLib"/"$artif"
basePack=info/kgeorgiy/java/advanced/implementor

javac -cp "$implLib" Implementor.java

cd ../../../../../

jar xf ../../"$artif" "$basePack/ImplerException.class" "$basePack/Impler.class" "$basePack/JarImpler.class"

solutionLib=info/kgeorgiy/ja/vircev/implementor

jar cmf "$solutionLib/implManifest.mf" "$solutionLib/Implementor.jar" "$solutionLib/*.class" "$basePack/*.class"

rm "$solutionLib/Implementor.class"