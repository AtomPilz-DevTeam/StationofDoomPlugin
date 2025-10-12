{
  description = "Java/Gradle development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { self, nixpkgs, ... }@inputs: 
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};
      jdk = pkgs.jdk21;
      gradle = pkgs.gradle_8.override { java = jdk; };
    in
    {

      devShells.x86_64-linux.default = pkgs.mkShell {
          nativeBuildInputs = [
            jdk
            gradle
          ];

         shellHook = ''
           echo "Development environment ready!"
           echo "JDK: $(java -version 2>&1 | head -n 1)"
           echo "Gradle: $(gradle --version | head -n 1)"
         '';
        };

    };
}
