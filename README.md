# github-source-code-identifiers

## Building on Ubuntu (Linux)

### Prerequitstics
`sudo apt install git`

`sudo apt install openjdk-17-jdk` or any other version.

`sudo apt-get install python3-distutils`

`sudo apt install build-essential`

Add `JAVA_HOME` to `~/.bashrc` (Linux):

`export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 export PATH=$JAVA_HOME/bin:$PATH`

Macos (`~/.zshrc`):
`export JAVA_HOME=$(/usr/libexec/java_home)`

### Clone

`git clone https://github.com/serenadeai/java-tree-sitter.git`
`cd java-tree-sitter`
`git clone https://github.com/tree-sitter/tree-sitter.git`

For each of the wanted tree-sitter grammars:
`git clone [clone-url]`
ex. `git clone https://github.com/tree-sitter/tree-sitter-java.git`

https://tree-sitter.github.io/tree-sitter/

### Build library

`python3 build.py -a x86_64 -o libjava-tree-sitter tree-sitter-java ...`
