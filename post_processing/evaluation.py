# confidence of A->B is condBA
# Wertebereich [0,inf[
# Werte nahe 1 sind uninteresting . Werte fern von 1 sind interesting
def lift(condBA, supB):
    return condBA / supB


# Wertebereich: 0.5,...,1,...,inf
def conv(supB, condBA):
    try:
        result = (1 - supB) / (1 - condBA)
        if result >= 0.5:
            return result
        else:
            return 0
    except:
        return 0


# leverage wertebereich: [-0.25,0.25]
# sup(A union B) - sup(A) x sup(B)
def leverage():
    return
