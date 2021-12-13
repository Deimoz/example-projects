#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <math.h>

typedef struct {
	float* matr;
	int* used; // -1 -> not used, other -> used
	size_t size;
} matrix;

const float eps = 0.000001;

int find_max(matrix* m, int col) {
	int answ = -1, s = m->size + 1;
	for (int i = 0; i < m->size; i++) {
		int curr = s * i + col;
		if (abs(m->matr[curr]) > eps && m->used[i] == -1 &&
			(answ == -1 || m->matr[curr] > m->matr[answ])) {
			answ = curr;
		}
	}
	return answ;
}

void subtract(matrix* m, int start_from, int start_to, float alpha) {
	for (int i = 0; i < m->size + 1; i++) {
		m->matr[start_to + i] -= m->matr[start_from + i] * alpha;
	}
}

void gauss_line(matrix* m, int col) {
	int num = find_max(m, col), s = m->size + 1;
	if (num != -1) {
		float curr_x = m->matr[num];
		for (int i = num - col; i < num - col + s; i++) {
			m->matr[i] /= curr_x;
		}
		for (int i = 0; i < m->size; i++) {
			int curr = s * i + col;
			if (curr != num && abs(m->matr[curr]) > eps) {
				subtract(m, num - col, curr - col, m->matr[curr]);
			}
			else if (curr == num) {
				m->used[i] = col;
			}
		}
	}
}

void gauss(matrix* m) {
	for (int i = 0; i < m->size; i++) {
		gauss_line(m, i);
	}
}

// -1 -> no answer, 0 -> normal, 1 -> all line is zero
int check_line_answ(matrix* m, int line) {
	int num_of_zeros = 0, start = line * (m->size + 1);
	for (int i = start; i < start + m->size; i++) {
		if (abs(m->matr[i]) <= eps) num_of_zeros++;
	}
	if (num_of_zeros == m->size) {
		if (m->matr[start + m->size] == 0) return 1;
		else return -1;
	}
	return 0;
}

// -1 -> no answer, 0 -> normal, 1 -> all line is zero
int check_answ(matrix* m) {
	int res = 0;
	for (int i = 0; i < m->size; i++) {
		int temp = check_line_answ(m, i);
		if (temp == 1) res = temp;
		else if (temp == -1) return temp;
	}
	return res;
}



int main(int argc, char* argv[]) {
	if (argc < 3) {
		printf("Not enough arguments");
		exit(1);
	}
	
	FILE* in = fopen(argv[1], "r");
	if (in == NULL) {
		printf("Failed to open input file");
		exit(1);
	}

	int n; fscanf(in, "%i", &n);

	matrix syst = { .matr = (float*)malloc(n * (n + 1) * sizeof(float)), .used = (int*)malloc(n*sizeof(int)), .size = n };
	if (syst.matr == NULL || syst.used == NULL) {
		printf("Failed to open output file");
		exit(1);
	}

	for (int i = 0; i < n; i++) {
		for (int j = 0; j < n + 1; j++) {
			fscanf(in, "%f", &syst.matr[(n + 1) * i + j]);
		}
	}

	for (int i = 0; i < n; i++) {
		syst.used[i] = -1;
	}

	gauss(&syst);

	FILE* out = fopen(argv[2], "w");
	if (out == NULL) {
		printf("Failed to open output file");
		exit(1);
	}

	int answ = check_answ(&syst);
	float* res;

	switch (answ) {
	case 0:
		res = (float*)malloc(n * sizeof(float));
		if (res == NULL) {
			printf("Failed to allocate memory");
			exit(1);
		}
		for (int line = 0; line < n; line++) {
			int col = syst.used[line];
			res[col] = syst.matr[line * (n + 1) + n];
		}
		for (int i = 0; i < n; i++) {
			fprintf(out, "%f ", res[i]);
		}
		free(res);
		break;
	case 1:
		fprintf(out, "many solutions");
		break;
	case -1:
		fprintf(out, "no solution");
	}

	free(syst.matr); free(syst.used);
	fclose(in); fclose(out);

	return 0;
}