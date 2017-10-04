package com.blito.salon.cli.runner;

import com.blito.salon.common.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.UUID;
import java.util.stream.IntStream;

/*
    @author Farzam Vatanzadeh
*/
@Component
public class SchemaGeneratorCommandLineRunner implements CommandLineRunner {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void run(String... args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter salon name");
        String salonName = scanner.nextLine();

        System.out.println("Enter salon address");
        String salonAddress = scanner.nextLine();

        System.out.println("Enter salon latitude");
        Double latitude = scanner.nextDouble();

        System.out.println("Enter salon longitude");
        Double longitude = scanner.nextDouble();

        Salon salon = new Salon(UUID.randomUUID().toString(),salonName,salonAddress,longitude,latitude);

        System.out.println("Please enter number of sections");
        Try<Integer> numberOfSectionsTry = Try.of(scanner::nextInt);
        while (numberOfSectionsTry.isFailure()) {
            scanner.next();
            System.out.println("Please enter a number");
            numberOfSectionsTry = Try.of(scanner::nextInt);
        }
        salon.setNumberOfSections(numberOfSectionsTry.get());

        IntStream.range(0,salon.getNumberOfSections())
                .forEach(dumpNumberOfSection -> {
                    System.out.println("Enter a section name");
                    String sectionName = scanner.next();

                    System.out.println("Specify number of rows");
                    Try<Integer> sectionNumberOfRowsTry = Try.of(scanner::nextInt);
                    while (sectionNumberOfRowsTry.isFailure()) {
                        scanner.next();
                        System.out.println("Please enter a number");
                        sectionNumberOfRowsTry = Try.of(scanner::nextInt);
                    }
                    Integer sectionNumberOfRows = sectionNumberOfRowsTry.get();
                    Section section = new Section(UUID.randomUUID().toString(),sectionName,sectionNumberOfRows,salon);
                    salon.getSections().add(section);

                    IntStream.range(0,section.getNumberOfRows())
                            .forEach(dumpNumberOfRows -> {
                                System.out.println("Enter row name/number");
                                String rowName = scanner.next();

                                System.out.println("Enter number of seats");
                                Try<Integer> numberOfSeatsTry = Try.of(scanner::nextInt);
                                while (numberOfSeatsTry.isFailure()) {
                                    scanner.next();
                                    System.out.println("Please enter a number");
                                    numberOfSeatsTry = Try.of(scanner::nextInt);
                                }

                                System.out.println("Enter first seat starting number");
                                Try<Integer> firstSeatStartingNumberTry = Try.of(scanner::nextInt);
                                while (numberOfSeatsTry.isFailure()) {
                                    scanner.next();
                                    System.out.println("Please enter a number");
                                    numberOfSeatsTry = Try.of(scanner::nextInt);
                                }

                                System.out.println("Enter direction (LTR RTL)");
                                Try<Direction> directionTry = Try.of(scanner::next).map(direction -> Enum.valueOf(Direction.class,direction));
                                while (directionTry.isFailure()) {
                                    scanner.next();
                                    System.out.println("Enter LTR or RTL");
                                    directionTry = Try.of(scanner::next).map(direction -> Enum.valueOf(Direction.class,direction));
                                }

                                Integer numberOfSeats = numberOfSeatsTry.get();
                                Integer firstSeatStartingNumber = firstSeatStartingNumberTry.get();
                                Integer lastSeatEndingNumber = firstSeatStartingNumber + numberOfSeats -1;
                                Row row = new Row(UUID.randomUUID().toString(),
                                        rowName,
                                        section,
                                        numberOfSeats,
                                        firstSeatStartingNumber,
                                        lastSeatEndingNumber, directionTry.get());

                                section.getRows().add(row);

                                IntStream.range(firstSeatStartingNumber,lastSeatEndingNumber)
                                        .forEach(seatNumber ->
                                            row.getSeats()
                                                    .add(new Seat(UUID.randomUUID().toString(),
                                                            String.valueOf(seatNumber),row)));
                            });
                });
        salon.getSections().stream().flatMap(section -> section.getRows().stream())
                .forEach(row ->
                    row.getSeats().forEach(seat -> {
                        seat.setNextUid(row.getSeats().stream()
                                .filter(seat1 -> seat1.getName().equals(String.valueOf(Integer.valueOf(seat.getName()) + 1)))
                                .findFirst().map(Seat::getUid).orElse(null));

                        seat.setPrevUid(row.getSeats().stream()
                                .filter(seat1 -> seat1.getName().equals(String.valueOf(Integer.valueOf(seat.getName()) - 1)))
                                .findFirst().map(Seat::getUid).orElse(null));
                    })
                );
        System.out.println(salon.toJson(objectMapper));
    }
}
