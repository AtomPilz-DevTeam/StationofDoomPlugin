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
      gradle = pkgs.gradle_9.override { java = jdk; };
    in
    {

      devShells.x86_64-linux.default = pkgs.mkShell {
          nativeBuildInputs = [
            jdk
            gradle
          ];

         shellHook = ''
           echo "Development environment ready!"
           echo "$(java -version 2>&1 | head -n 1)"
           echo "$(gradle --version | head -n 3 | tail -n 1)"
         '';
        };

    };
}
