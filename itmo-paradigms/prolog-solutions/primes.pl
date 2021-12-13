/*too many code*/

build_divisors(1, I, R, R) :- !.

build_divisors(N, I, R0, R) :-
    0 is mod(N,I),
	  prime(I),
	  N1 is N / I,
	  build_divisors(N1, I, [I | R0], R).

build_divisors(N, I, R0, R) :-
    I1 is I + 1,
	  build_divisors(N, I1, R0, R).

prime_divisors(1, []) :- !.

prime_divisors(N, R) :-
		integer(N),
    build_divisors(N, 2, [], R1),
    reverse(R, R1), !.

count_divisors([], LAST, C, C) :- !.

count_divisors([H | T], LAST, C, N) :-
		H >= LAST,
		prime(H),
		C1 is C * H,
		count_divisors(T, H, C1, N).

prime_divisors(N, R) :-
		count_divisors(R, 2, 1, N).

add_to_composite(N, I, D) :- I > N, !.

add_to_composite(N, I, D) :-
    assert(composite(I)),
    I1 is I + D,
    add_to_composite(N, I1, D).

build_primes(N, I, NUM) :- I > N, !.

build_primes(N, I, NUM) :-
		composite(I),
		I1 is I + 1,
    build_primes(N, I1, NUM).

build_primes(N, I, NUM) :-
    assert(prime(I)),
    assert(nth_prime(NUM, I)),
    IC is I + I,
    add_to_composite(N, IC, I),
    I1 is I + 1,
    NUM1 is NUM + 1,
    build_primes(N, I1, NUM1).

init(MAX_N) :-
	build_primes(MAX_N, 2, 1).