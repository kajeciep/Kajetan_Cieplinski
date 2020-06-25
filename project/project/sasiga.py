import random
from statistics import median
from pysat.formula import WCNF
from math import ceil
from random import shuffle
import matplotlib.pyplot as plt
import numpy as np
import argparse

########

tournament_fight_size = 2
individual_island_selection_pressure = True

########


class Individual:
    def __init__(self, bitstring, island_no):
        self.bitstring = bitstring
        self.island_no = island_no
        self.fitness = calc_fitness(self.bitstring)

    def set_bitstring(self, bitstring):
        self.bitstring = bitstring

    def update_fitness(self):
        self.fitness = calc_fitness(self.bitstring)

    def buffer_fitness(self):
        self.fitness = -1

    def apply_mutation(self):
        for i in range(len(self.bitstring)):
            if mutation_rate > random.uniform(0, 1):
                if self.bitstring[i] == 1:
                    self.bitstring[i] = 0
                else:
                    self.bitstring[i] = 1
        if mutation_of_selection_rate > random.uniform(0, 1):
            self.island_no = random.randint(0, (no_of_islands - 1))


def get_fitness(individual):
    return individual.fitness


def apply_crossover(individual_1, individual_2):
    crossover_point = int((individual_size / 2))
    child_1 = []
    child_2 = []
    child_1 = child_1 + individual_1.bitstring[0:crossover_point]
    child_2 = child_2 + individual_2.bitstring[0:crossover_point]
    child_1 = child_1 + individual_2.bitstring[crossover_point:individual_size]
    child_2 = child_2 + individual_1.bitstring[crossover_point:individual_size]
    return child_1, child_2


def initialise_islands(island_number, initial_population):
    islands = []
    new_island = []
    for j in range(initial_population):
        new_island.append(generate_individual(0))
    islands.append(new_island)
    for i in range(1, island_number):
        new_island = []
        islands.append(new_island)

    return islands


def generate_individual(island_no):
    bitstring = []
    if all_zeroes_bitstrings:
        for i in range(individual_size):
            bitstring.append(0)
    else:
        for i in range(individual_size):
            bitstring.append(random.randint(0, 1))
    return Individual(bitstring, island_no)


def evaluate_clause(bitstring, clause):
    for variable in clause:
        if variable < 0:
            if bitstring[abs(variable)-1] == 0:
                return True
        else:
            if bitstring[variable-1] == 1:
                return True
    return False


def calc_fitness(bitstring):
    fitness_value = 0
    if max_sat_problem:
        for clause in cnf.clauses:
            if evaluate_clause(bitstring, clause):
                fitness_value += 1
        return fitness_value
    if local_optimum_at_zeroes:
        if 1 not in bitstring:
            return 5
    if two_max_instead_of_leading:
        no_of_ones = bitstring.count(1)
        no_of_zeroes = bitstring.count(0)
        if no_of_ones > bitstring.count(0):
            return no_of_ones
        else:
            return no_of_zeroes
    else:
        for i in range(1, len(bitstring) + 1):
            current_count = 1
            for j in range(0, i):
                current_count = current_count * bitstring[j]
            fitness_value += current_count
    return fitness_value


def update_island_fitness(island):
    for i in range(len(island)):
        island[i].update_fitness()


def tournament_selection(island, island_no):
    temp_island = island
    temp_island.sort(key=get_fitness, reverse=True)
    tournament_winners = []
    if individual_island_selection_pressure:
        selective_pressure = int(ceil((len(island) - 1) * (upper_selective_rate - (lower_selection_rate * island_no) / no_of_islands)))
    else:
        selective_pressure = len(temp_island) - 1
    for i in range(0, len(temp_island)):
        contestant_1 = random.randint(0, selective_pressure)
        contestant_2 = random.randint(0, selective_pressure)
        if temp_island[contestant_1].fitness > temp_island[contestant_2].fitness:
            tournament_winners.append(temp_island[contestant_1])
        else:
            tournament_winners.append(temp_island[contestant_2])
    return tournament_winners


def evolve_island(island, island_no):
    elite_population = tournament_selection(island, island_no)
    if len(elite_population) % 2 != 0:
        odd_number = True
    else:
        odd_number = False
    children = []
    midpoint = int(len(elite_population) / 2)
    for i in range(0, midpoint):
        child1, child2 = apply_crossover(elite_population[i*2], elite_population[i*2+1])
        child_individual_1 = Individual(child1, island_no - 1)
        child_individual_2 = Individual(child2, island_no - 1)
        child_individual_1.apply_mutation()
        child_individual_2.apply_mutation()
        children.append(child_individual_1)
        children.append(child_individual_2)
    if odd_number:
        children.append(elite_population[len(elite_population) - 1])
    return children


def global_selection(islands):
    temp_population = []
    island_selective_pressures = []
    for i in range(1, no_of_islands + 1):
        s_p = int(ceil((len(islands[i-1]) - 1) * (upper_selective_rate - (lower_selection_rate * i) / no_of_islands)))
        island_selective_pressures.append(s_p)

    for i in range(0, no_of_initial_individuals):
        current_tournament = []
        for j in range(0, no_of_islands):
            if len(islands[j]) != 0:
                island_contestant = random.randint(0, island_selective_pressures[j])
                current_tournament.append((islands[j])[island_contestant])
        shuffle(current_tournament)
        current_tournament.sort(key=get_fitness, reverse=True)
        temp_population.append(current_tournament[0])

    if len(temp_population) % 2 != 0:
        odd_number = True
    else:
        odd_number = False
    children = []
    midpoint = int(len(temp_population) / 2)
    for i in range(0, midpoint):
        child1, child2 = apply_crossover(temp_population[i*2], temp_population[i*2+1])
        child_individual_1 = Individual(child1, (temp_population[i*2]).island_no)
        child_individual_2 = Individual(child2, (temp_population[i*2+1]).island_no)
        child_individual_1.apply_mutation()
        child_individual_2.apply_mutation()
        children.append(child_individual_1)
        children.append(child_individual_2)
    if odd_number:
        children.append(temp_population[len(temp_population) - 1])
    return children


def split_for_emigration(island, island_no):
    true_island_no = island_no - 1
    emigrators = []
    current_island_length = len(island)
    i = 0
    while i < current_island_length:
        if island[i].island_no != true_island_no:
            emigrators.append(island.pop(i))
            current_island_length -= 1
        else:
            i += 1
    return island, emigrators


def sasiga():
    """
    Initialise Population
    Calculate fitness

    For no of generations:
        For each island:
            Tournament Selection
            Take selected individuals:
                Perform crossover
                Perform mutation
                Select for emigration
        Process emigration
        Trim populations (???)
        Calculate fitness
    """
    average_best_individual = []
    island_populations_by_all_experiment = []
    whole_population = []
    for hjk in range(0, no_of_experiments):
        island_populations_by_experiment = []
        experiment_best_individual_by_generation = []
        whole_population = initialise_islands(no_of_islands, no_of_initial_individuals)
        curr_generation = []
        curr_generation_pop = []
        for island in whole_population:
            curr_island = []
            for individual in island:
                curr_island.append(individual.fitness)
            island_max = max(curr_island, default=-1)
            curr_generation.append(island_max)
            curr_generation_pop.append(len(island))
        experiment_best_individual_by_generation.append(max(curr_generation, default=-1))
        island_populations_by_experiment.append(curr_generation_pop)

        for i in range(0, no_of_generations):
            new_islands = []
            all_emigrators = []
            island_no = 1
            if global_tournament and (0.1 > random.uniform(0, 1)):
                all_emigrators = global_selection(whole_population)
                for island in whole_population:
                    new_islands.append([])
            else:
                for island in whole_population:
                    print(len(island))
                    new_island = evolve_island(island, island_no)
                    print(len(new_island))
                    new_island, emigrators = split_for_emigration(new_island, island_no)
                    print(str(len(new_island)) + " " + str(len(emigrators)))
                    new_islands.append(new_island)
                    all_emigrators = all_emigrators + emigrators
                    print("---" + str(len(all_emigrators)) + " emigrators---")
                    island_no += 1
            print("---generation_" + str(i+1) + "---")
            while len(all_emigrators) > 0:
                destination = all_emigrators[0].island_no
                new_islands[destination].append(all_emigrators.pop(0))
            whole_population = new_islands
            for island in whole_population:
                update_island_fitness(island)

            if ((i + 1) % generation_split) == 0:
                curr_generation = []
                curr_generation_pop = []
                for island in whole_population:
                    curr_island = []
                    for individual in island:
                        curr_island.append(individual.fitness)
                    island_max = max(curr_island, default=-1)
                    curr_generation.append(island_max)
                    curr_generation_pop.append(len(island))
                experiment_best_individual_by_generation.append(max(curr_generation, default=-1))
                island_populations_by_experiment.append(curr_generation_pop)
        average_best_individual.append(experiment_best_individual_by_generation)
        island_populations_by_all_experiment.append(island_populations_by_experiment)

    """!Testing Site!"""

    for island in whole_population:
        elite_individuals = []
        print(len(island))
        for i in island:
            print(i.fitness, end=" ")
            if i.fitness == individual_size:
                if i.bitstring.count(1):
                    elite_individuals.append(1)
                else:
                    elite_individuals.append(0)
        print("")
        print(elite_individuals)
        print("-------")
    list_of_averages = []
    for i in range(0, int(no_of_generations / generation_split) + 1):
        curr_sum = []
        for j in range(0, len(average_best_individual)):
            curr_sum.append(average_best_individual[j][i])
        list_of_averages.append(curr_sum)
    island_population_averages = []
    for i in range(0, no_of_islands):
        island_pop_ave_by_generation = []
        for j in range(0, int(no_of_generations / generation_split) + 1):
            island_pop_by_gen = []
            for k in range(0, no_of_experiments):
                island_pop_by_gen.append(island_populations_by_all_experiment[k][j][i])
            median_population = median(island_pop_by_gen)
            island_pop_ave_by_generation.append(median_population)
        island_population_averages.append(island_pop_ave_by_generation)

    print("List of Averages")
    print(list_of_averages)
    generations_incr = np.arange(0, no_of_generations + 1, generation_split).tolist()
    fig = plt.figure(1, figsize=(9, 6))
    ax = fig.add_subplot(111)
    ax.set_xticklabels(generations_incr)
    ax.set_title('Boxplot on LO Problem, Self Adaptive Algorithm (30 Experiments)')
    plt.ylabel('Fitness')
    plt.xlabel('Generation')
    bp = ax.boxplot(list_of_averages)
    plt.show()
    
    plt.clf()
    for island in island_population_averages:
        plt.plot(generations_incr, island)
    plt.xlabel('Generation')
    plt.ylabel('Island_population')
    plt.title('Median Island Populations, by Generation, Self Adaptive Algorithm (30 Experiments)')
    plt.show()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-op', '--op', help='Optimisation problem input', required=False, default="LO")
    parser.add_argument('-gt', '--gt', help='Global Tournament', required=False, default=0)
    parser.add_argument('-mr', '--mr', help='Mutation Rate', required=False, default=-1.0)
    parser.add_argument('-sru', '--sru', help='Selection Rate Upper Limit', required=False, default=0.7)
    parser.add_argument('-srl', '--srl', help='Selection Rate Lower Limit', required=False, default=0.4)
    parser.add_argument('-pop', '--pop', help='Population Size', required=False, default=250)
    parser.add_argument('-gs', '--gs', help='Generational Split', required=False, default=10)
    parser.add_argument('-e', '--e', help='Number of Experiments', required=False, default=30)
    parser.add_argument('-g', '--g', help='Number of Generations', required=False, default=250)
    parser.add_argument('-l', '--l', help='Bitstring Length', required=False, default=20)
    parser.add_argument('-i', '--i', help='Number of Islands', required=False, default=20)
    args = vars(parser.parse_args())
    if args['op'] == "MS":
        no_of_islands = int(args['i'])
        wcnf = WCNF(from_file='brock200_2.clq.wcnf')
        cnf = wcnf.unweighted()
        individual_size = cnf.nv
        if float(args['mr']) == -1.0:
            mutation_rate = 1 / individual_size
        else:
            mutation_rate = float(args['mr'])
        no_of_initial_individuals = int(args['pop'])
        no_of_generations = int(args['g'])
        generation_split = int(args['gs'])
        no_of_experiments = int(args['e'])
        global_tournament = int(args['gt'])
        max_sat_problem = True
        two_max_instead_of_leading = False
        all_zeroes_bitstrings = False
        local_optimum_at_zeroes = False
        mutation_of_selection_rate = 0.05
        upper_selective_rate = float(args['sru'])
        lower_selection_rate = float(args['srl'])
        sasiga()
    elif args['op'] == "LO":
        no_of_islands = int(args['i'])
        individual_size = int(args['l'])
        if float(args['mr']) == -1.0:
            mutation_rate = 1 / individual_size
        else:
            mutation_rate = float(args['mr'])
        no_of_initial_individuals = int(args['pop'])
        no_of_generations = int(args['g'])
        generation_split = int(args['gs'])
        no_of_experiments = int(args['e'])
        global_tournament = int(args['gt'])
        max_sat_problem = False
        two_max_instead_of_leading = False
        all_zeroes_bitstrings = False
        local_optimum_at_zeroes = False
        mutation_of_selection_rate = 0.05
        upper_selective_rate = float(args['sru'])
        lower_selection_rate = float(args['srl'])
        print(upper_selective_rate)
        print(lower_selection_rate)
        sasiga()
    elif args['op'] == "PLO":
        no_of_islands = int(args['i'])
        individual_size = int(args['l'])
        if float(args['mr']) == -1.0:
            mutation_rate = 1 / individual_size
        else:
            mutation_rate = float(args['mr'])
        no_of_initial_individuals = int(args['pop'])
        no_of_generations = int(args['g'])
        generation_split = int(args['gs'])
        no_of_experiments = int(args['e'])
        global_tournament = int(args['gt'])
        max_sat_problem = False
        two_max_instead_of_leading = False
        all_zeroes_bitstrings = False
        local_optimum_at_zeroes = True
        mutation_of_selection_rate = 0.05
        upper_selective_rate = float(args['sru'])
        lower_selection_rate = float(args['srl'])
        sasiga()
    elif args['op'] == "PLOZ":
        no_of_islands = int(args['i'])
        individual_size = int(args['l'])
        if float(args['mr']) == -1.0:
            mutation_rate = 1 / individual_size
        else:
            mutation_rate = float(args['mr'])
        no_of_initial_individuals = int(args['pop'])
        no_of_generations = int(args['g'])
        generation_split = int(args['gs'])
        no_of_experiments = int(args['e'])
        global_tournament = int(args['gt'])
        max_sat_problem = False
        two_max_instead_of_leading = False
        all_zeroes_bitstrings = True
        local_optimum_at_zeroes = True
        mutation_of_selection_rate = 0.05
        upper_selective_rate = float(args['sru'])
        lower_selection_rate = float(args['srl'])
        sasiga()
    elif args['op'] == "2M":
        no_of_islands = int(args['i'])
        individual_size = int(args['l'])
        if float(args['mr']) == -1.0:
            mutation_rate = 1 / individual_size
        else:
            mutation_rate = float(args['mr'])
        no_of_initial_individuals = int(args['pop'])
        no_of_generations = int(args['g'])
        generation_split = int(args['gs'])
        no_of_experiments = int(args['e'])
        global_tournament = int(args['gt'])
        max_sat_problem = False
        two_max_instead_of_leading = True
        all_zeroes_bitstrings = False
        local_optimum_at_zeroes = False
        mutation_of_selection_rate = 0.05
        upper_selective_rate = float(args['sru'])
        lower_selection_rate = float(args['srl'])
        sasiga()
    else:
        print("Unrecognised problem entered. Exiting...")
