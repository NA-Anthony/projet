#!/bin/bash
echo "                          -------      Chargement de la compilation      -------"

# Arrêter le script en cas d'erreur
set -e

# Définir les répertoires source, temporaire et de travail
src_dir="/Users/nakanyanthony/Documents/GitHub/projet/Framework/src"
temp_src="/Users/nakanyanthony/Documents/GitHub/projet/Framework/temp-src"
work_dir="/Users/nakanyanthony/Documents/GitHub/projet/Test"
lib_dir="/Users/nakanyanthony/Documents/GitHub/projet/Framework/lib"

# Créer le répertoire temporaire
mkdir -p "$temp_src/bin"
cd "$src_dir"

find "$src_dir" -name "*.java" -exec cp {} "$temp_src" \;

cd "$temp_src"

# Copier les fichiers de la bibliothèque dans le dossier temporaire
rsync -av --progress "$lib_dir/" "$temp_src/lib/"

# Récupérer tous les fichiers JAR en une seule chaîne
classpath=$(echo "$lib_dir"/*.jar | tr ' ' ':')

# Aller dans le répertoire source et compiler les fichiers .java
javac -d "$temp_src/bin" -cp "$classpath" *.java

# Créer un fichier JAR
jar -cvf "$work_dir/lib/myServlet.jar" -C "$temp_src/bin" .

# Nettoyer les fichiers temporaires
find "$temp_src" -name "*.java" -delete

rsync -av --progress "$temp_src/" "$work_dir"

rm -rf "$temp_src"

echo "                              -------         Compilation terminée         -------"
