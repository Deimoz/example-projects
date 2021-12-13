package info.kgeorgiy.ja.vircev.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentQuery {
    private final Comparator<Student> localComparator = Comparator.comparing(Student::getLastName).reversed()
            .thenComparing(Comparator.comparing(Student::getFirstName).reversed())
            .thenComparing(Student::getId);

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getStudentsPersonalInfo(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getStudentsPersonalInfo(students, Student::getLastName);
    }

    // :NOTE: unify getGroups and getLastNames
    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return students.stream()
                .map(Student::getGroup)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getStudentsPersonalInfo(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream()
                .map(Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream()
                .max(Student::compareTo)
                .map(Student::getFirstName)
                .orElse("");
    }

    // :NOTE: copy paste
    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return students.stream()
                .sorted(Comparator.comparing(Student::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return students.stream()
                .sorted(localComparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findStudentsByName(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findStudentsByName(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return students.stream()
                .filter(student -> student.getGroup().equals(group))
                .sorted(localComparator)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByGroup(students, group).stream()
                .collect(Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        (first, second) -> first.compareTo(second) <= 0 ? first : second
                ));
    }

    private List<String> getStudentsPersonalInfo(List<Student> students, Function<Student, String> mapFunc) {
        return students.stream()
                .map(mapFunc)
                .collect(Collectors.toList());
    }

    private List<Student> findStudentsByName(Collection<Student> students, Function<Student, String> nameFunc, String name) {
        return students.stream()
                .filter(student -> nameFunc.apply(student).equals(name))
                .sorted(localComparator)
                .collect(Collectors.toList());
    }

    /*private Stream<String> abstractStreamMap(List<Student> students, Function<Student, String> mapFunc) {
        return students.stream().map(mapFunc);
    }*/
}
