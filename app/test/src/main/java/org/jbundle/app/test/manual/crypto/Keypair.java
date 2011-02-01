package org.jbundle.app.test.manual.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class Keypair {

    public Keypair()
    {
        
    }
    
    public static final void main(String[] args)
    {
        Keypair pwd = new Keypair();
        pwd.gen();
    }
    
    public void gen()
    {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");

            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);

//    The following code illustrates how to use a specific, additionally seeded SecureRandom object:

//        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
  //      random.setSeed(userSeed);
   //    keyGen.initialize(1024, random);

//    Since no other parameters are specified when you call the above algorithm-independent initialize method, it is up to the provider what to do about the algorithm-specific parameters (if any) to be associated with each of the keys. The provider may use precomputed parameter values or may generate new values.
//    Algorithm-Specific Initialization

//    For situations where a set of algorithm-specific parameters already exists (such as "community parameters" in DSA), there are two initialize methods that have an AlgorithmParameterSpec argument. Suppose your key pair generator is for the "DSA" algorithm, and you have a set of DSA-specific parameters, p, q, and g, that you would like to use to generate your key pair. You could execute the following code to initialize your key pair generator (recall that DSAParameterSpec is an AlgorithmParameterSpec):

//        DSAParameterSpec dsaSpec = new DSAParameterSpec(p, q, g);
  //      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
   //    random.setSeed(userSeed);
     //  keyGen.initialize(dsaSpec, random);

//        NOTE: The parameter named p is a prime number whose length is the modulus length ("size"). Therefore, you don't need to call any other method to specify the modulus length. 

//Generating the Pair of Keys

//    The final step is actually generating the key pair. No matter which type of initialization was used (algorithm-independent or algorithm-specific), the same code is used to generate the key pair:

            KeyPair pair = keyGen.generateKeyPair();
            
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            
            System.out.println("enc " + privateKey.getEncoded());
            System.out.println("for " + privateKey.getFormat());
            System.out.println("alg " + privateKey.getAlgorithm());
            
            System.out.println("enc " + publicKey.getEncoded());
            System.out.println("for " + publicKey.getFormat());
            System.out.println("alg " + publicKey.getAlgorithm());
            
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }
    
}
