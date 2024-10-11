#!/bin/bash
echo "                          -------      Chargement de la compilation      -------"

# Arrêter le script en cas d'erreur
set -e

# Définir les répertoires source, temporaire et de travail
src_dir="/Users/nakanyanthony/Documents/GitHub/projet/Framework/src"
temp_src="/Users/nakanyanthony/Documents/GitHub/projet/Framework/temp-src"
work_dir="/Users/nakanyanthony/Documents/GitHub/projet/Test"

# Créer le répertoire temporaire
mkdir -p "$temp_src"
cd "$src_dir"

find "$src_dir" -name "*.java" -exec cp {} "$temp_src" \;

cd "$temp_src"

# Aller dans le répertoire source et compiler les fichiers .java
javac -d "$temp_src"/bin *.java

# # Créer un fichier JAR
jar -cvf "$work_dir/lib/myServlet.jar" -C "$temp_src"/bin .

find "$temp_src" -name "*.java" -delete

rsync -av --progress "$temp_src/" "$work_dir"

rm -rf "$temp_src"

echo "                              -------         Compilation terminée         -------"