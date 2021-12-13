#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
#include <fstream>

using namespace std;

class BigInteger {
private:
	static const int base = 1000000000;
	vector<int> number;
	bool negative, is_NaN;

	void delete_zeros() {
		while (number.size() > 1 && number.back() == 0) {
			number.pop_back();
		}
		if (number.size() == 1 && number[0] == 0) negative = false;
	}
public:
	BigInteger(string num) {
		negative = false; is_NaN = false;
		if (num.size() == 0) {
			number.resize(1, 0);
		}
		else {
			int start = 0;
			if (num[0] == '-') {
				start++; negative = true;
			}
			int size = (num.size() - start) / 9, mod = (num.size() - start) % 9;
			number.resize(size);
			if (mod != 0) {
				number.push_back(atoi(num.substr(start, mod).c_str()));
				start += mod;
			}
			for (int i = start, j = size - 1; i < num.size(); i += 9, j--) {
				if (i + 9 <= num.size()) {
					number[j] = atoi(num.substr(i, 9).c_str());
				}
			}
		}
		delete_zeros();
	}

	BigInteger(int num) {
		negative = false; is_NaN = false;
		if (num < 0) negative = true;
		num = abs(num);
		number.push_back(num % base);
		num /= base;
		if (num != 0) number.push_back(num);
	}

	int size() {
		return number.size();
	}

	friend bool operator ==(const BigInteger& a, const BigInteger& b) {
		if (a.negative != b.negative) return false;
		if (a.number.size() != b.number.size()) return false;
		for (int i = 0; i < a.number.size(); i++) {
			if (a.number[i] != b.number[i]) return false;
		}
		return true;
	}

	friend bool operator <(const BigInteger& a, const BigInteger& b) {
		if (a.negative) {
			if (b.negative) return (-b < -a);
			return true;
		}
		if (b.negative) return false;
		if (a.number.size() != b.number.size()) return a.number.size() < b.number.size();
		for (int i = a.number.size() - 1; i >= 0; i--) {
			if (a.number[i] != b.number[i]) return a.number[i] < b.number[i];
		}
		return false;
	}

	friend bool operator !=(const BigInteger& a, const BigInteger& b) {
		return !(a == b);
	}

	friend bool operator >(const BigInteger& a, const BigInteger& b) {
		return !(a < b) && (a != b);
	}

	friend bool operator <=(const BigInteger& a, const BigInteger& b) {
		return !(a > b);
	}

	friend bool operator >=(const BigInteger& a, const BigInteger& b) {
		return !(a < b);
	}

	BigInteger operator=(const BigInteger right) {
		if (this == &right) {
			return *this;
		}
		negative = right.negative;
		number = right.number;
		is_NaN = right.is_NaN;
		return *this;
	}

	BigInteger operator=(const int right) {
		BigInteger res(right);
		*this = res;
		return *this;
	}

	BigInteger operator-() const {
		BigInteger temp("0");
		temp.number = (*this).number;
		temp.is_NaN = (*this).is_NaN;
		temp.negative = !(*this).negative;
		return temp;
	}

	friend BigInteger operator+(const BigInteger& a, const BigInteger& b) {
		if (a.negative) {
			if (b.negative) return -(-a + (-b));
			return b - (-a);
		}
		if (b.negative) {
			return a - (-b);
		}
		int carry = 0;
		BigInteger res = a;
		for (int i = 0; i < max(res.number.size(), b.number.size()); i++) {
			if (res.number.size() == i) res.number.push_back(0);
			if (b.number.size() == i) break;
			res.number[i] += carry;
			if (i < b.number.size()) res.number[i] += b.number[i];
			carry = res.number[i] >= base;
			if (carry != 0) res.number[i] -= base;
		}
		if (carry == 1) res.number.push_back(carry);
		return res;
	}

	friend BigInteger operator-(const BigInteger& a, const BigInteger& b) {
		if (a.negative) {
			if (b.negative) return (-b) - (-a);
			return -(-a + b);
		}
		if (b.negative) {
			return a + (-b);
		}
		if (a < b) return -(b - a);
		BigInteger res = a;
		int carry = 0;
		for (int i = 0; i < b.number.size() || carry != 0; i++) {
			res.number[i] -= carry;
			if (i < b.number.size()) res.number[i] -= b.number[i];
			carry = res.number[i] < 0;
			if (carry != 0) res.number[i] += base;
		}
		res.delete_zeros();
		return res;
	}

	// b >= 0 && b < 1000000000
	friend BigInteger operator*(BigInteger a, int b) {
		int carry = 0;
		for (int i = 0; i < a.size() || carry != 0; i++) {
			if (i == a.size()) a.number.push_back(0);
			long long next_num = carry + 1ll * a.number[i] * b;
			a.number[i] = next_num % base;
			carry = next_num / base;
		}
		a.delete_zeros();
		return a;
	}

	friend BigInteger operator*(int b, BigInteger a) {
		return a * b;
	}

	friend BigInteger operator*(const BigInteger &a, const BigInteger &b) {
		BigInteger c("0"); c.number.resize(a.number.size() + b.number.size(), 0);
		if ((a.negative && !b.negative) || (!a.negative && b.negative)) c.negative = true;
		for (int i = 0; i < a.number.size(); i++) {
			int carry = 0;
			for (int j = 0; j < b.number.size() || carry != 0; j++) {
				long long next_num = 0;
				if (j < b.number.size()) next_num = 1ll * a.number[i] * b.number[j];
				next_num += c.number[i + j] + carry;
				c.number[i + j] = next_num % base;
				carry = next_num / base;
			}
		}
		c.delete_zeros();
		return c;
	}

	void create_cell() {
		if (size() == 0) number.push_back(0);
		else {
			vector<int> new_number(size() + 1);
			for (int i = 0; i < number.size(); i++) {
				new_number[i + 1] = number[i];
			}
			number = new_number;
		}
	}

	friend BigInteger operator/(const BigInteger &a, const BigInteger &b) {
		BigInteger curr("0");
		if (b.number.size() == 1 && b.number[0] == 0) {
			curr.is_NaN = true;
			return curr;
		}
		BigInteger res("0"), diviser = b;
		res.number.resize(a.number.size(), 0);
		curr.number.resize(0);
		bool res_sign = false;
		if ((a.negative && !b.negative) || (!a.negative && b.negative)) res_sign = true;
		diviser.negative = false;

		for (int i = a.number.size() - 1; i >= 0; i--) {
			curr.create_cell();
			curr.number[0] = a.number[i];
			curr.delete_zeros();
			if (curr < diviser) continue;
			int l = 0, r = base;
			while (r - l > 1) {
				int m = (l + r) / 2;
				if (curr < diviser * m) r = m;
				else l = m;
			}
			res.number[i] = l;
			curr = curr - diviser * l;
		}

		res.negative = res_sign;
		res.delete_zeros();
		return res;
	}

	friend BigInteger operator%(const BigInteger& a, const BigInteger& b) {
		BigInteger curr("0");
		if (b.number.size() == 1 && b.number[0] == 0) {
			curr.is_NaN = true;
			return curr;
		}
		BigInteger diviser = b;
		curr.number.resize(0);
		diviser.negative = false;

		for (int i = a.number.size() - 1; i >= 0; i--) {
			curr.create_cell();
			curr.number[0] = a.number[i];
			curr.delete_zeros();
			if (curr < diviser) continue;
			int l = 0, r = base;
			while (r - l > 1) {
				int m = (l + r) / 2;
				if (curr < diviser * m) r = m;
				else l = m;
			}
			curr = curr - diviser * l;
		}

		if (a.negative) curr = diviser - curr;
		curr.delete_zeros();
		return curr;
	}

	friend BigInteger sqrt(BigInteger a) {
		if (a.negative) {
			a.is_NaN = true;
			return a;
		}
		BigInteger l("0"), r("0"), one("1"), two("2");
		r.number.resize(a.size() / 2 + 1);
		for (int i = 0; i < r.size(); i++) {
			r.number[r.size() - i - 1] = a.number[a.size() - i - 1];
		}
		r = r + one;
		while (r - l > one) {
			BigInteger m = (r + l) / two;
			if (m * m > a) r = m;
			else l = m;
		}
		return l;
	}

	string to_str() {
		string res = "";
		if (is_NaN) {
			res = "NaN";
			return res;
		}
		if (negative) res = "-";
		res += to_string(number[number.size() - 1]);
		for (int i = number.size() - 2; i >= 0; i--) {
			string num = to_string(number[i]), nulls;
			for (int j = 0; j < 9 - num.size(); j++) {
				nulls += '0';
			}
			res += nulls + num;
		}
		return res;
	}

	friend ostream& operator<< (ostream& out, BigInteger& b) {
		out << b.to_str();
		return out;
	}

	friend ofstream& operator<< (ofstream& out, BigInteger& b) {
		out << b.to_str();
		return out;
	}
};

int main(int argc, char* argv[]) {
	if (argc < 3) {
		cout << "Not enough arguments";
		return 1;
	}
	ifstream in(argv[1]);
	if (!in.is_open()) {
		cout << "Failed to open input file";
		return 1;
	}
	ofstream out(argv[2]);
	if (!out.is_open()) {
		cout << "Failed to open output file";
		in.close();
		return 1;
	}
	string first, op;
	in >> first >> op;
	BigInteger left(first);
	if (op == "#") {
		left = sqrt(left);
	}
	else {
		string second;
		in >> second;
		BigInteger right(second);
		if (op == "+") left = left + right;
		else if(op == "-") left = left - right;
		else if (op == "*") left = left * right;
		else if (op == "/") left = left / right;
		else if (op == "%") left = left % right;
		else if (op == "<") left = left < right;
		else if (op == ">") left = left > right;
		else if (op == "==") left = left == right;
		else if (op == ">=") left = left >= right;
		else if (op == "<=") left = left <= right;
		else if (op == "!=") left = left != right;
	}
	out << left << endl;
	in.close(); out.close();
	return 0;
}
