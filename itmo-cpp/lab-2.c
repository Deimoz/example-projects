#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <string.h>
#include <inttypes.h>

struct personal_info {
	char surname[21], name[21], middle_name[21];
	int64_t phone_number;
};

int compare_info(const struct personal_info* first, const struct personal_info* second) { 
	int cmp = strcmp(first->surname, second->surname);
	if (cmp != 0) {
		return cmp; // cmp > 0 => first > second, cmp < 0 => first < second
	}
	cmp = strcmp(first->name, second->name);
	if (cmp != 0) {
		return cmp; // cmp > 0 => first > second, cmp < 0 => first < second
	}
	cmp = strcmp(first->middle_name, second->middle_name);
	if (cmp != 0) {
		return cmp; // cmp > 0 => first > second, cmp < 0 => first < second
	}
	return first->phone_number - second->phone_number; // res >= 0 => first >= second, res < 0 => first < second
}

void swap_info(struct personal_info* first, struct personal_info* second) {
	struct personal_info temp;
	temp = *first;
	*first = *second;
	*second = temp;
}

int get_partition(struct personal_info* info, int l, int r) {
	int part = (r + l) / 2;
	struct personal_info p = info[part]; 
	int i = l, j = r;
	while (i <= j) {
		while (compare_info(&info[i], &p) < 0) {
			i++;
		}
		while (compare_info(&info[j], &p) > 0) {
			j--;
		}
		if (i >= j) {
			break;
		}
		swap_info(&info[i++], &info[j--]);
	}
	return j;
}

void sort_info(struct personal_info* info, int l, int r) { // [l, r] -> включительно
	start:
	if (l < r) {
		int p = get_partition(info, l, r);
		if (p - l > r - p) {
			sort_info(info, l, p);
			l = p + 1;
		}
		else {
			sort_info(info, p + 1, r);
			r = p;
		}
		goto start;
	}
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
	int size_of_mas = 10;
	struct personal_info* info = (struct personal_info*)malloc(size_of_mas * sizeof(struct personal_info));
	if (info == NULL) {
		printf("Failed to allocate memory");
		exit(1);
	}
	int size = 0;
	while (!feof(in)) {
		if (size >= size_of_mas) {
			size_of_mas *= 2;
			info = (struct personal_info*)realloc(info, size_of_mas * sizeof(struct personal_info));
			if (info == NULL) {
				printf("Failed to allocate memory");
				exit(1);
			}
		}
		fscanf(in, "%s %s %s %lld", info[size].surname, info[size].name, info[size].middle_name, &info[size].phone_number);
		size++;
	}
	fclose(in);

	sort_info(info, 0, size - 1);

	FILE* out = fopen(argv[2], "w");
	if (out == NULL) {
		printf("Failed to open output file");
		exit(1);
	}
	for (int i = 0; i < size; i++) {
		fprintf(out, "%s %s %s %lld\n", info[i].surname, info[i].name, info[i].middle_name, info[i].phone_number);
	}
	free(info); info = NULL;
	fclose(out);
	return 0;
}