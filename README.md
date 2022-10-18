# code-generator
A highly customizable Java based code generator I created to manually fill databases quicker, and to get a better understanding of Configuration

#Usage:

The program is contained within the `CodeGenerator-{version}.jar` file. Simply execute the `.jar` in an empty directory
and 3 files will be generated. `app.config`, `code_configuration.txt`, and `input.txt`. They are essential for the program to operate.

After the files are generated, a prompt will appear showing you the configured settings. On first launch, these will be the default settings. You may choose to continue with the execution to see an example, or cancel and make any necessary changes. 

Once the program successfully executes, a new file `output.txt` will be generated with the processed and formatted text, or "code".

Any errors should throw a popup with the stack trace.

#Input
`input.txt` is the input file. They MUST MATCH the schema of `code_generation.txt`. If you do not, you WILL get errors. Here's a few things to take note of.
* `Comments` - By starting a line with `#`, it stops the program from reading that line. Note: This works on all 3 input files used by the program.
* `Saved Word` - By starting a line with `$`, it tells the program to remember the entire contents of that line. This is if you need that one specific string referenced for every input line.
* `Separators` - Used to separate variables in `input.txt`. Example: `'Jeff Bezos,Amazon,1994'` will see that there are 2 valid separators (if you're using the default separator).
* `Variable Indicators` - Used to denote variables in `code_configuration.txt`. The program prints the indexed value of the line. Example: `@2@` will print `Amazon` from the example above.
* `Booleans` - If you want to use a boolean in `input.txt`, you can use `f`, `t`, `false`, and `true`. All will be accepted.


#Configuration 
`app.config` contains some simple configurations. We will quickly go over what each one does.
* `separator` (Default: ,) - Indicates the symbol/character you want to identify the end of a variable, and start of the next variable.
* `variable_indicator` (Default: @) - This is a unique identifier that is used to wrap/denote variables. Similar to a '$' when it comes to PHP variables, except in this case, the `variable_indicator` is present at both the beginning and end of a variable, and instead of variable names, we use indexes (more in the 'Code Configuration' section).
* `total_values` (Default: 6) - This is simply to make processing and formatting the data significantly easier. It's possible to do this dynamically, but that wasn't the point of this exercise.
* `count_start` (Default: 1) - The value of this variable determines at which unique identification number the program will begin at. It's referenced by a for loop that begins at the set value. It does not offset the amount of lines processed. Just the number the for loop starts at.
* `trim_values` (Default: false) - WARNING! This part is not fully implemented yet. Results are not guaranteed.

#Code Configuration
`code_configuration.txt` is the template for what you want the output of EACH LINE to look like. Along with some information, it includes some example data for us to work with. Let's go through the uncommented lines one by one.
* `[` - This one is easy to explain. At the beginning of every line, print a `[`.
* `'name' => '@1@',` - This one is a little more complicated. It contains a variable. In this program, as mentioned earlier, variables in this file are wrapped in `variable_indicators` to denote that they're supposed to be filled with information from `input.txt`. The `1` means that it wants the first value (all the text before the first comma) from the current line in `input.txt`.
* `'personal_id' => @#@,` - This is similar to the one above, however instead of using a variable from the current line in `input.txt`, it references the current value of `count`, which starts at whatever `count_start` is set to and increments by 1 until the program has completed iterating through `input.txt`.
* `'date_of_birth' => '@2@',` - Same operation as `name`, except it's referencing the value of variable `2`, instead of `1`.
* `'talents' => '@3@',` - Same as `name`, and `date_of_birth`, except it's referencing the value of variable `3`.
* `'wage' => '@4@',` - Same as `name`, `date_of_birth`, and `talents`, except it's referencing the value of variable `4`.
* `'category' => '@$@',` - Similar to `personal_id`, instead of referencing `count`, it references the `saved_word`.
* `'start_date' => '@5@',` - Same as `name`, `date_of_birth`, `talents`, and `wage`, except it's referencing the value of variable `5`.
* `'went_to_post_secondary' => '@6@',` - Same as `name`, `date_of_birth`, `talents`, `wage`, and `start_date`, except it's referencing the value of variable `6`.
* `],` - This one is also easy to explain. It's the last readable line in `code_configuration.txt`, which means as soon as this part is processed, the program moves on to the next line.
