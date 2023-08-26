# confidence of A->B is condBA
# Wertebereich [0,inf[
# Werte nahe 1 sind uninteresting . Werte fern von 1 sind interesting
def lift(condBA, supB):
    return abs(1 - (condBA / supB))


# Wertebereich: 0.5,...,1,...,inf
def conv(supB, condBA):
    divisor = 1 - condBA
    if divisor == 0:
        return 0
    result = (1 - supB) / (1 - condBA)
    if result < 0.5:
        return 0
    return abs(1 - result)


# leverage wertebereich: [-0.25,0.25]
# sup(A union B) - sup(A) x sup(B)
def leverage():
    return 0
