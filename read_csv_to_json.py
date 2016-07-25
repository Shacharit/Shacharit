import csv
import json


def create_gift_dict(row, gift_index):
    gift = {}
    fields = ["type", "url", "cta male2male", "cta male2female", "cta female2male", "cta female2female", "greeting male2male", "greeting male2female", "greeting female2male", "greeting female2female"]
    for i in range(0, 10):
        gift[fields[i]] = row[gift_index + i]
    return gift


# Download the events table spreadsheet as a tsv (not a csv!), move it to the project's directory and run this file.
# Once you have the .json file that represents the table, run:
# firebase-import -f https://il-hackathon.firebaseio.com/ -m -j <path to .json file> on it and it will update the table
# in Firebase.
# You can find installation instructions and run instructions in:
# 1. http://blog.teamtreehouse.com/install-node-js-npm-mac
# 2. https://github.com/firebase/firebase-import
def read_csv_to_json(tsv_filename, json_filename, number_of_gifts, gift_index, event_definitions_index):
    json_file = open(json_filename, 'w')
    table = {'events': []}
    with open(tsv_filename, 'rb') as tsv_file:
        csv_reader = csv.reader(tsv_file, delimiter='\t'
                                )
        for i, row in enumerate(csv_reader):
            if i == 0:
                field_names = row[0:event_definitions_index]
                event_definitions = row[event_definitions_index:]
                continue

            row_dictionary = {}
            j = 0
            # Handling regular fields
            for field in field_names:
                if j >= gift_index:
                        break
                else:
                    row_dictionary[field] = row[j]
                j += 1

            # Handling gift fields
            for k in range(0, number_of_gifts):
                row_dictionary[('gift' + str(k + 1))] = create_gift_dict(row, j)
                j += 10

            current_event_definitions = {}
            for definition in event_definitions:
                if row[j] == '1':
                    current_event_definitions[definition] = True
                j += 1
            row_dictionary['event_definitions'] = current_event_definitions
            table['events'].append(row_dictionary)

    json.dump(table, json_file)


read_csv_to_json('events_table.tsv', 'events_table.json', 3, 8, 38)