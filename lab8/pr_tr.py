import numpy as np
import decimal
import copy

L1 = [0, 1, 1, 0, 1, 0, 0, 0, 0, 0]
L2 = [1, 0, 0, 1, 0, 0, 0, 0, 0, 0]
L3 = [0, 1, 0, 0, 0, 0, 1, 0, 0, 0]
L4 = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L5 = [0, 0, 0, 0, 0, 1, 1, 0, 0, 0]
L6 = [0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
L7 = [0, 0, 0, 0, 1, 1, 1, 1, 1, 1]
L8 = [0, 0, 0, 0, 0, 0, 1, 0, 1, 0]
L9 = [0, 0, 0, 0, 0, 0, 1, 0, 0, 1]
L10 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

L = np.array([L1, L2, L3, L4, L5, L6, L7, L8, L9, L10])


ITERATIONS = 100


def getM(L):
    M = np.zeros([10, 10], dtype=float)
    # number of outgoing links
    c = np.zeros([10], dtype=int)

    ## TODO 1 compute the stochastic matrix M
    for i in range(0, 10):
        c[i] = sum(L[i])

    for i in range(0, 10):
        for j in range(0, 10):
            if L[j][i] == 0:
                M[i][j] = 0
            else:
                M[i][j] = 1.0 / c[j]
    return M


def calculateSolution(initialVector,initialMatrix,isTrustRank):
    for x in range(ITERATIONS):
        if x % 2 == 0:
            current = initialVector[0]
            previous = initialVector[1]
        else:
            current = initialVector[1]
            previous = initialVector[0]
        for i in range(10):
            Sum = 0
            for j in range(10):
                # CALCULATING SUMS FROM THE EQUATIONS
                Sum += previous[j] / c[j] * initialMatrix[j][i]

                # trustrank, but removed the connections 3->7 and 1->5 (indexes: 2->6, 0->4)
            if isTrustRank:
                current[i] = q * previous[i] + (1 - q) * Sum
            else:
                current[i] = q + (1 - q) * Sum
            # version from Wekepedia
            # current[i] = (1-q)/len(L) + q * Sum
        detonator = 1
        # convergence is assumed
        if x == ITERATIONS - 1:
            X = {i: current[i] for i in range(len(current))}
            Y = {k: v for k, v in sorted(X.items(), key=lambda item: item[1])}
            for y, yv in Y.items():
                print(y, ": ", yv)

print("Matrix L (indices)")
print(L)

M = getM(L)

print("Matrix M (stochastic matrix)")
print(M)

### TODO 2: compute pagerank with damping factor q = 0.15
### Then, sort and print: (page index (first index = 1 add +1) : pagerank)
### (use regular array + sort method + lambda function)
print("PAGERANK")

'''
adequatly to the formula for PageRank with damping factor
PageRank(di)=vi=q+(1-q)SUM_j:dj->di_vj/cj
'''
pageRank = [0] * len(L)

# c vector contains the number of links on each page
c = [sum(L[i]) for i in range(len(L))]
print(c)

for i in range(len(c)):
    if c[i] == 0:
        # had to be done due to division by zero
        c[i] += 0.1

# copyM = getM(L)
# for i in range(0, 10):
#      copyM[i][i] -= 1

# print(copyM)
# invCopy = np.linalg.pinv(copyM)
# invCopy = np.linalg.inv((copyM))
# x = np.dot(invCopy , np.array([1., 1., 1., 1., 1., 1., 1., 1., 1., 1.]))
# x = np.linalg.solve(np.array(copyM), np.array([1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0]))
# print(x)
q = 0.15
# pr = np.zeros([10], dtype=float)
# print(len(L))


# PR vector for pagerank
v = [1 / len(L)] * len(L)
V = [v, v]

calculateSolution(V,L,0)
# number of iteration 100 for certain covergence
# for x in range(ITERATIONS):
#     if x % 2 == 0:
#         current = V[0]
#         current_tr = Vtr[0]
#         current_tr2 = Vtr2[0]
#         previous = V[1]
#         previous_tr = Vtr[1]
#     else:
#         current = V[1]
#         current_tr = Vtr[1]
#         previous = V[0]
#         previous_tr = Vtr[0]
#     for i in range(10):
#         Sum = 0
#         Sum_tr = 0
#         for j in range(10):
#             # CALCULATING SUMS FROM THE EQUATIONS
#             # pagerank
#             Sum += previous[j] / c[j] * L[j][i]
#             # trustrank
#             Sum_tr += previous_tr[j] / c[j] * L[j][i]
#             # trustrank, but removed the connections 3->7 and 1->5 (indexes: 2->6, 0->4)
#         current[i] = q + (1 - q) * Sum
#         current_tr[i] = q * previous_tr[i] + (1 - q) * Sum_tr
#         # version from Wekepedia
#         # current[i] = (1-q)/len(L) + q * Sum
#     print(current_tr)
#     detonator = 1
#     # convergence is assumed
#     if x == ITERATIONS - 1:
#
#         DONE = []
#         X = {i: current[i] for i in range(len(current))}
#         Xtr = {i: current_tr[i] for i in range(len(current))}
#         Y = {k: v for k, v in sorted(X.items(), key=lambda item: item[1])}
#         Ytr = {k: v for k, v in sorted(Xtr.items(), key=lambda item: item[1])}
#         print(Y)
#
#         for y,yv in Y.items():
#             print(y,": ",yv)
#         #     for x, xv in X.items():
#         #         if xv == y:
#         #             if x not in DONE:
#         #                 DONE.append(x)
#         #                 print(x, ": ", xv)
#         # print(current)
#         # print(current_tr)

print("TRUSTRANK (DOCUMENTS 1 AND 2 ARE GOOD)")

### TODO 3: compute trustrank with damping factor q = 0.15
### Documents that are good = 1, 2 (indexes = 0, 1)
### Then, sort and print: (page index (first index = 1, add +1) : trustrank)
### (use regular array + sort method + lambda function)
# TR vector for trustranks
trusted = [0, 1]
vtr = [1 / len(trusted) if i in trusted else 0 for i in range(len(L))]
Vtr = [vtr, vtr]
Vtr2 = copy.deepcopy(Vtr)

calculateSolution(Vtr,L,1)

### TODO 4: Repeat TODO 3 but remove the connections 3->7 and 1->5 (indexes: 2->6, 0->4)
### before computing trustrank
print("TRUSTRANK (WITH removed connections 3->7 and 1->5)")

# second matrix for trust rank 2
L2 = copy.deepcopy(L)
L2[0][4] = 0
L2[2][6] = 0
#print(Vtr2)
calculateSolution(Vtr2,L2,1)
