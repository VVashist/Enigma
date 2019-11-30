# Enigma : http://www-inst.eecs.berkeley.edu/~cs61b/fa19/materials/proj/proj1/index.html 
Achieved encryption and decryption of text by developing a Java-based simulator of the Enigma machines used in WWII  | CS 61B - Data Structures - UC Berkeley 

### Background
You may have heard of the Enigma machines that Germany used during World War II to encrypt its military communications. If you have not, I recommend you read the wikipedia page on them, or similar resource, especially the part about design and operation. This project involves building a simulator for a generalized version of this machine (which itself had several different versions.) Your program will take descriptions of possible initial configurations of the machine and messages to encode or decode (the Enigma algorithms were reciprocal, meaning that encryption is its own inverse operation.)

The Enigmas effect a substitution cipher on the letters of a message. That is, at any given time, the machine performs a permutation—a one-to-one mapping—of the alphabet onto itself. The alphabet consists solely of the 26 letters in one case (there were various conventions for spaces and punctuation).

Plain substitution ciphers are easy to break (you've probably seen puzzles in newspapers that consist of breaking such ciphers). The Enigma, however, implements a progressive substitution, different for each subsequent letter of the message. This made decryption considerably more difficult.

The device consists of a simple mechanical system of (partially) interchangeable rotors (Walzen) that sit side-by-side on a shaft and make electrical contact with each other. Most of these rotors have 26 contacts on both sides, which are wired together internally so as to effect a permutation of signals coming in from one side onto the contacts on the other (and the inverse permutation when going in the reverse direction). To the left of the rotors, one could select one of a set of reflectors (Umkehrwalzen), with contacts on their right sides only, and wired to connect half of those contacts to the other half. A signal starting from the rightmost rotor enters through one of the 26 possible contacts, flows through wires in the rotors, "bounces" off the reflector, and then comes back through the same rotors (in reverse) by a different route, always ending up being permuted to a letter position different from where it started; that is, the permutation was always a derangement. (This was a significant cryptographic weakness, as it turned out. It doesn't really do a would-be code-breaker any good to know that some letters in an encrypted message might be the same as the those in the plaintext if he doesn't know which ones. But it does a great deal of good to be able to eliminate possible decryptions because some of their letters are the same as in the plaintext.)

Each rotor and each reflector implements a different permutation, and the overall effect depends on their configuration: which rotors and reflector are used, what order they are placed in the machine, and which rotational position they are initially set to. This configuration is the first part of the secret key used to encrypt or decrypt a message. In what follows, we'll refer to the selected reflector and rotors in a machine's configuration as 1 through N, with 1 being the reflector, and N the rightmost rotor. In our simulator, N will be a configuration parameter. In actual Enigma machines, it was fixed for any given model (the Kreigsmarine (Navy) used N=5 (four rotors and reflector) and the Wehrmacht used N=4.)

The overall permutation changes with each successive letter because some of the rotors rotate after encrypting a letter. Each rotor has a circular ratchet on its right side and an "alphabet ring" on its left side that fits over the ratchet of the rotor to its left. Before a letter of a message is translated, a spring-loaded pawl (lever)—one to the right of each rotating rotor—tries to engage the ratchet on the right side of its rotor and thus rotate its rotor by one position, changing the permutation performed by the rotor. Thus, pawls always try to engage with the ratchet of their own rotor. The lever on the rightmost rotor (N) always succeeds, so that rotor N (the "fast" rotor) rotates one position before each character. The pawls pushing the other rotors, however, are normally blocked from engaging their rotors by the alphabet ring on the left side of the rotor to their right

This ring usually holds the pawl away from its own ratchet, preventing the rotor wheel to its left from moving. However, the rings have notches in them (either one or two in the original Enigma machines), and when the pawl is positioned over a notch in the ring for the rotor to its right, it slips through to its own rotor and pushes it forward. A "feature" of the design called "double stepping" (corrected in other versions of the Enigma, since it reduced the period of the cipher) is that when a pawl is in a notch, it also moves the notch itself and the rotor the notch is connected to. Since the notch for rotor i is connected to rotor i, when the pawl of rotor i−1 slips through into the notch for rotor i, rotors on both sides of the pawl move (so rotor i−1 and rotor i move).
