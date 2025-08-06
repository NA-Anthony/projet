#!/bin/bash
echo "                          -------      Chargement de la compilation      -------"

# Arrêter le script en cas d'erreur
set -e

# Définir les répertoires source, temporaire et de travail
path=$(pwd)
src_dir="$path/src"
temp_src="$path/temp-src"
lib_dir="$path/lib"
config_file="../Test/config.properties"
work_dir=$(grep "^work_dir=" "$config_file" | cut -d '=' -f2)

if [ -z "$work_dir" ]; then
    echo "Sélectionnez le dossier de travail..."
    work_dir=$(osascript -e 'tell application "Finder" to choose folder with prompt "Sélectionnez le dossier de travail"' -e 'POSIX path of result')

    # Supprimer le dernier "/" s'il est présent
    work_dir=$(echo "$work_dir" | sed 's:/*$::')

    # Sauvegarde dans le fichier de configuration
    echo "work_dir=$work_dir" >> "$config_file"
fi

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

rm -rf "$work_dir/bin"

echo "                              -------         Compilation terminée         -------"
