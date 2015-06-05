import random

def main():
    number = random.randint(6, 12)
    print 'the number is  {0}'.format(number)
    number_list = makelist(number) # return a list of "number" ints
    sorted_list = sorted(number_list) # sort the list

    output_string = str(sorted_list[0])
    for i in range(1, number - 1):
        concat = " {0}".format(str(sorted_list[i]))
        output_string += concat # create the output string
    print output_string

def makelist(number):
    empty_list = []
    for i in range(0, number): #create a list of "number" ints
        rand_number = random.randint(1, 100)
        empty_list.append(rand_number)
    return empty_list


if __name__ == "__main__":
        main()
