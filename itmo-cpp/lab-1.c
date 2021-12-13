#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <string.h>
#include <inttypes.h>

enum coding {
	UTF8 = 0,
	UTF8_WITH_BOM,
	UTF16_LE,
	UTF16_BE,
	UTF32_LE,
	UTF32_BE
};

struct char_mas {
	unsigned char* val;
	int size;
};

struct int32_mas {
	int32_t* val;
	int size;
};

const unsigned char* UTF8_BOM = "\xEF\xBB\xBF";
const unsigned char* UTF16_BE_BOM = "\xFE\xFF";
const unsigned char* UTF16_LE_BOM = "\xFF\xFE";
const unsigned char* UTF32_BE_BOM = "\x00\x00\xFE\xFF";
const unsigned char* UTF32_LE_BOM = "\xFF\xFE\x00\x00";

void check_char_memory(unsigned char* mas) {
	if (mas == NULL) {
		printf("Failed to allocate memory");
		exit(1);
	}
}

void check_int32_memory(int32_t* mas) {
	if (mas == NULL) {
		printf("Failed to allocate memory");
		exit(1);
	}
}

int32_t UTF8_to_unicode(struct char_mas* data, int* UTF8_error_flag, int* i) {
	int num_of_bytes = 0;
	if (data->val[*i] >> 7 == 0b0) {
		num_of_bytes = 1;
	}
	else if (data->val[*i] >> 5 == 0b110) {
		num_of_bytes = 2;
	}
	else if (data->val[*i] >> 4 == 0b1110) {
		num_of_bytes = 3;
	}
	else if (data->val[*i] >> 3 == 0b11110) {
		num_of_bytes = 4;
	}
	else {
		*i += 1;
		return data->val[*i - 1] | 0xDC80;
	}

	if (data->size < *i + num_of_bytes) {
		UTF8_error_flag = 1;
		*i += num_of_bytes;
		return data->val[*i] | 0xDC80;
	}

	for (int j = *i + 1; j < *i + num_of_bytes; j++) {
		if (data->val[j] >> 6 != 0b10) {
			UTF8_error_flag = 1;
			*i += num_of_bytes;
			return data->val[*i] | 0xDC80;
		}
	}

	int32_t unicode_code = data->val[*i] & 0b01111111;

	switch (num_of_bytes) {
	case 2:
		unicode_code = ((data->val[*i] & 0b00011111) << 6) + (data->val[*i + 1] & 0b00111111);
		break;
	case 3:
		unicode_code = ((data->val[*i] & 0b00001111) << 12) + ((data->val[*i + 1] & 0b00111111) << 6) + (data->val[*i + 2] & 0b00111111);
		break;
	case 4:
		unicode_code = ((data->val[*i] & 0b00000111) << 18) + ((data->val[*i + 1] & 0b00111111) << 12) +
			((data->val[*i + 2] & 0b00111111) << 6) + (data->val[*i + 3] & 0b00111111);
	}
	*i += num_of_bytes;

	return unicode_code;
}

int32_t UTF16_BE_to_unicode(struct char_mas* data, int* i) {
	int num_of_bytes = 2;
	if (data->val[*i] >> 2 == 0b110110) {
		num_of_bytes = 4;
	}

	if (data->size <= *i + num_of_bytes) {
		*i = data->size;
		return 0xFFFD;
	}

	int32_t unicode_code = 0;

	switch (num_of_bytes) {
	case 2:
		if (data->val[*i] >> 2 == 0b110111) {
			*i += num_of_bytes;
			return 0xFFFD;
		}
		unicode_code = (data->val[*i] << 8) + data->val[*i + 1];
		break;
	case 4:
		if (data->val[*i + 2] >> 2 != 0b110111) {
			*i += num_of_bytes;
			return 0xFFFD;
		}
		unicode_code = ((data->val[*i] - 0b11011000) << 18) + (data->val[*i + 1] << 10) + ((data->val[*i + 2] - 0b11011100) << 8) + data->val[*i + 3];
	}
	*i += num_of_bytes;
	return unicode_code;
}

int32_t UTF16_LE_to_unicode(struct char_mas* data, int* i) {
	int num_of_bytes = 2;
	if (data->val[*i] >> 2 == 0b110110) {
		num_of_bytes = 4;
	}

	if (data->size <= *i + num_of_bytes) {
		*i = data->size;
		return 0xFFFD;
	}

	int32_t unicode_code = 0;

	switch (num_of_bytes) {
	case 2:
		if (data->val[*i] >> 2 == 0b110111) {
			*i += num_of_bytes;
			return 0xFFFD;
		}
		unicode_code = (data->val[*i + 1] << 8) + data->val[*i];
		break;
	case 4:
		if (data->val[*i + 2] >> 2 != 0b110111) {
			*i += num_of_bytes;
			return 0xFFFD;
		}
		unicode_code = ((data->val[*i] - 0b11011000) << 8) + data->val[*i + 1] + ((data->val[*i + 2] - 0b11011100) << 18) + (data->val[*i + 3] << 10);
	}
	*i += num_of_bytes;

	return unicode_code;
}

int32_t UTF32_BE_to_unicode(struct char_mas* data, int* i) {
	int num_of_bytes = 4;
	if (data->size <= *i + num_of_bytes) {
		*i = data->size;
		return 0xFFFD;
	}
	int32_t unicode_code = data->val[*i] << 24 + data->val[*i + 1] << 16 + data->val[*i + 2] << 8 + data->val[*i + 3];
	*i += num_of_bytes;

	return unicode_code;
}

int32_t UTF32_LE_to_unicode(struct char_mas* data, int* i) {
	int num_of_bytes = 4;
	if (data->size <= *i + num_of_bytes) {
		*i = data->size;
		return 0xFFFD;
	}
	int32_t unicode_code = data->val[*i + 3] << 24 + data->val[*i + 2] << 16 + data->val[*i + 1] << 8 + data->val[*i];
	*i += num_of_bytes;

	return unicode_code;
}

struct char_mas unicode_to_UTF32_BE(int32_t unicode_code) {
	struct char_mas bytes = { (unsigned char*)malloc(4 * sizeof(char)), 4 };
	check_char_memory(bytes.val);
	int32_t temp = 0xFF000000;
	for (int i = 0; i < 4; i++) {
		bytes.val[i] = (unsigned char)((unicode_code & temp) >> (24 - i * 8));
		temp /= 0x100;
	}
	return bytes;
}

struct char_mas unicode_to_UTF32_LE(int32_t unicode_code) {
	struct char_mas bytes = { (unsigned char*)malloc(4 * sizeof(char)), 4 };
	check_char_memory(bytes.val);
	int32_t temp = 0xFF;
	for (int i = 0; i < 4; i++) {
		bytes.val[i] = (unicode_code & temp) >> (8 * i);
		temp *= 0x100;
	}
	return bytes;
}

struct char_mas unicode_to_UTF16_BE(int32_t unicode_code) {
	struct char_mas bytes;
	if (unicode_code <= 0xFFFF) {
		bytes.size = 2;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		bytes.val[0] = (unicode_code & 0xFF00) >> 8;
		bytes.val[1] = unicode_code & 0x00FF;
	}
	else {
		bytes.size = 4;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		int32_t high_surr = ((unicode_code - 0x10000) >> 10) + 0xD800, low_surr = (unicode_code & 0x3FF) + 0xDC00;
		bytes.val[0] = (high_surr & 0xFF00) >> 8; bytes.val[1] = high_surr & 0x00FF;
		bytes.val[2] = (low_surr & 0xFF00) >> 8; bytes.val[3] = low_surr & 0x00FF;
	}
	return bytes;
}

struct char_mas unicode_to_UTF16_LE(int32_t unicode_code) {
	struct char_mas bytes;
	if (unicode_code <= 0xFFFF) {
		bytes.size = 2;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		bytes.val[0] = unicode_code & 0x00FF;
		bytes.val[1] = (unicode_code & 0xFF00) >> 8;
	}
	else {
		bytes.size = 4;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		int32_t high_surr = ((unicode_code - 0x10000) >> 10) + 0xD800, low_surr = (unicode_code & 0x3FF) + 0xDC00;
		bytes.val[0] = high_surr & 0x00FF; bytes.val[1] = (high_surr & 0xFF00) >> 8;
		bytes.val[2] = low_surr & 0x00FF; bytes.val[3] = (low_surr & 0xFF00) >> 8;
	}
	return bytes;
}

struct char_mas unicode_to_UTF8(int32_t unicode_code) {
	struct char_mas bytes;
	if (unicode_code > 0x10FFFF) {
		unicode_code = 0xFFFD;
	}
	if (unicode_code <= 0x7F) {
		bytes.size = 1;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		bytes.val[0] = unicode_code & 0b01111111;
	}
	else if (unicode_code <= 0x7FF) {
		bytes.size = 2;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		bytes.val[0] = ((unicode_code >> 6) & 0b00011111) | 0b11000000;
		bytes.val[1] = (unicode_code & 0b111111) | 0b10000000;
	}
	else if (unicode_code <= 0xFFFF) {
		bytes.size = 3;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		bytes.val[0] = ((unicode_code >> 12) & 0b00001111) | 0b11100000;
		bytes.val[1] = ((unicode_code >> 6) & 0b111111) | 0b10000000;
		bytes.val[2] = (unicode_code & 0b111111) | 0b10000000;
	}
	else {
		bytes.size = 4;
		bytes.val = (unsigned char*)malloc(bytes.size * sizeof(char));
		check_char_memory(bytes.val);
		bytes.val[0] = ((unicode_code >> 18) & 0b00000111) | 0b11110000; bytes.val[1] = ((unicode_code >> 12) & 0b111111) | 0b10000000;
		bytes.val[2] = ((unicode_code >> 6) & 0b111111) | 0b10000000; bytes.val[3] = (unicode_code & 0b111111) | 0b10000000;
	}
	return bytes;
}

int32_t get_UTF_to_unicode(const struct char_mas* data, int* i, int* UTF8_error_flag, int code) {
	switch (code) {
	case UTF32_BE:
		return UTF32_BE_to_unicode(data, i);
	case UTF32_LE:
		return UTF32_LE_to_unicode(data, i);
	case UTF16_BE:
		return UTF16_BE_to_unicode(data, i);
	case UTF16_LE:
		return UTF16_LE_to_unicode(data, i);
	case UTF8:
	case UTF8_WITH_BOM:
		return UTF8_to_unicode(data, UTF8_error_flag, i);
	}
	exit(1);
}

struct char_mas get_unicode_to_UTF(int32_t unicode_code, int code) {
	switch (code) {
	case UTF32_BE:
		return unicode_to_UTF32_BE(unicode_code);
	case UTF32_LE:
		return unicode_to_UTF32_LE(unicode_code);
	case UTF16_BE:
		return unicode_to_UTF16_BE(unicode_code);
	case UTF16_LE:
		return unicode_to_UTF16_LE(unicode_code);
	case UTF8:
	case UTF8_WITH_BOM:
		return unicode_to_UTF8(unicode_code);
	}
	exit(1);
}

struct int32_mas UTF_to_unicode(const struct char_mas* data, int code) {
	int i = 0, size_of_mas = 10;
	size_t length = data->size;

	struct int32_mas unicode = { (int32_t*)malloc(size_of_mas * sizeof(int32_t)), 0 };
	check_int32_memory(unicode.val);

	while (i < length) {
		int UTF8_error_flag = NULL, start = i;
		int32_t unicode_code = get_UTF_to_unicode(data, &i, &UTF8_error_flag, code);

		if (UTF8_error_flag) {
			if (unicode.size + (i - start) >= size_of_mas) {
				size_of_mas = 2 * size_of_mas + (i - start);
				unicode.val = (int32_t*)realloc(unicode.val, size_of_mas * sizeof(int32_t));
				check_int32_memory(unicode.val);
			}
			for (int j = start; j < i; j++) {
				unicode.val[unicode.size++] = data->val[j] | 0xDC80;
			}
			continue;
		}

		if (unicode.size >= size_of_mas) {
			size_of_mas *= 2;
			unicode.val = (int32_t*)realloc(unicode.val, size_of_mas * sizeof(int32_t));
			check_int32_memory(unicode.val);
		}

		unicode.val[unicode.size++] = unicode_code;
	}
	return unicode;
}

struct char_mas unicode_to_UTF(struct int32_mas* unicode, int code) {
	int size_of_mas = unicode->size + 4;
	struct char_mas new_data = { (unsigned char*)malloc(size_of_mas * sizeof(char)), 0 };
	check_char_memory(new_data.val);
	switch (code) {
	case UTF32_BE:
		strncpy(new_data.val, UTF32_BE_BOM, 4);
		new_data.size += 4;
		break;
	case UTF32_LE:
		strncpy(new_data.val, UTF32_LE_BOM, 4);
		new_data.size += 4;
		break;
	case UTF16_BE:
		strncpy(new_data.val, UTF16_BE_BOM, 2);
		new_data.size += 2;
		break;
	case UTF16_LE:
		strncpy(new_data.val, UTF16_LE_BOM, 2);
		new_data.size += 2;
		break;
	case UTF8_WITH_BOM:
		strncpy(new_data.val, UTF8_BOM, 3);
		new_data.size += 3;
	}

	for (int i = 0; i < unicode->size; i++) {
		struct char_mas symbol = get_unicode_to_UTF(unicode->val[i], code);
		if (symbol.size + new_data.size >= size_of_mas) {
			size_of_mas = (symbol.size + new_data.size) * 2;
			new_data.val = (unsigned char*)realloc(new_data.val, size_of_mas * sizeof(char));
			check_char_memory(new_data.val);
		}
		for (int j = 0; j < symbol.size; j++) {
			new_data.val[new_data.size++] = symbol.val[j];
		}
		free(symbol.val);
	}
	new_data.val = (unsigned char*)realloc(new_data.val, new_data.size * sizeof(char));
	return new_data;
}

struct char_mas decode(struct char_mas* data, int code_output) {
	struct int32_mas unicode;
	int code_input;

	if (strncmp(data->val, UTF32_BE_BOM, 4) == 0) {
		code_input = UTF32_BE; data->val += 4; data->size -= 4;
	}
	else if (strncmp(data->val, UTF32_LE_BOM, 4) == 0) {
		code_input = UTF32_LE; data->val += 4; data->size -= 4;
	}
	else if (strncmp(data->val, UTF8_BOM, 3) == 0) {
		code_input = UTF8_WITH_BOM; data->val += 3; data->size -= 3;
	}
	else if (strncmp(data->val, UTF16_BE_BOM, 2) == 0) {
		code_input = UTF16_BE; data->val += 2; data->size -= 2;
	}
	else if (strncmp(data->val, UTF16_LE_BOM, 2) == 0) {
		code_input = UTF16_LE; data->val += 2; data->size -= 2;
	}
	else {
		code_input = UTF8;
	}

	unicode = UTF_to_unicode(data, code_input);

	struct char_mas new_data = unicode_to_UTF(&unicode, code_output);

	free(unicode.val); unicode.val = NULL;

	switch (code_input) {
	case UTF32_BE:
		data->val -= 4; data->size += 4;
		break;
	case UTF32_LE:
		data->val -= 4; data->size += 4;
		break;
	case UTF16_BE:
		data->val -= 2; data->size += 2;
		break;
	case UTF16_LE:
		data->val -= 2; data->size += 4;
		break;
	case UTF8_WITH_BOM:
		data->val -= 3; data->size += 3;
	}

	return new_data;
}

int main(int argc, char* argv[]) {
	if (argc < 4) {
		printf("Not enough arguments");
		exit(1);
	}
	FILE* in = fopen(argv[1], "r");
	if (in == NULL) {
		printf("Failed to open input file");
		exit(1);
	}
	int size_of_mas = 10;
	struct char_mas data = { (unsigned char*)malloc(size_of_mas * sizeof(char)) , 0 };
	while (fscanf(in, "%c", &data.val[data.size++]) != EOF) {
		if (data.size >= size_of_mas) {
			size_of_mas *= 2;
			data.val = (unsigned char*)realloc(data.val, size_of_mas * sizeof(char));
			check_char_memory(data.val);
		}
	}
	if (data.size >= size_of_mas) {
		data.val = (unsigned char*)realloc(data.val, data.size * sizeof(char));
	}
	check_char_memory(data.val);
	--data.size;
	int count = 0;
	struct char_mas new_data = decode(&data, atoi(argv[3]));
	free(data.val); data.val = NULL;
	fclose(in);

	FILE* out = fopen(argv[2], "w");
	if (out == NULL) {
		printf("Failed to open output file");
		exit(1);
	}
	for (int i = 0; i < new_data.size; i++) {
		fprintf(out, "%c", new_data.val[i]);
	}
	free(new_data.val); new_data.val = NULL;
	fclose(out);
	return 0;
}