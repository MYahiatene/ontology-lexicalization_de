# confidence of A->B is condBA
# Wertebereich [0,inf[
# Werte nahe 1 sind uninteresting . Werte fern von 1 sind interesting
# conf(A->B)/sup(B)
# conf(A->B) = P(B|A)
import math


def lift(supA, supAB, supB):
    conf = supAB / supA
    return (conf / supB)


# Wertebereich: 0.5,...,1,...,inf
def conv(supAB, supA, supB):
    conf = supAB / supA
    divisor = 1 - conf
    if divisor == 0:
        return math.inf
    return (1 - supB) / divisor


# leverage wertebereich: [-0.25,0.25]
# sup(A union B) - sup(A) x sup(B)
def leverage(supAB, supA, supB):
    return supAB - (supA * supB)

#  cosine ->1  ist gut!!!
# lift  <-1 ist gut!!!

# cosine 0 bad
# lift 1 bad

#supA = 0.4