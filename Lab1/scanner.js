
$(document).ready(function(){

  user_type_definition_keywords = [/^define /];
  var_declaration_keywords = [/^int /, /^real /];
  assign_stmt_keywords = [/=/];
  input_stmt_keywords = [/^input\(/];
  output_stmt_keywords = [/^output\(/];
  loop_stmt_keywords = [/while\(/];
  if_stmt_keywords = [/if\(/];


  $(".run-btn").on("click", function(){
    var input_code = $(".code-input").val();
    var input_lines = input_code.split(/;|\n/);
    input_lines.forEach(function(item, index){
      if(item === "" || item === " "){
        input_lines.splice(index, 1);
      }
    });

    input_lines.forEach(function(line, index){
      user_type_definition_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          words = line.split(/ |\(|\)|\( | \)/);
          console.log(words);
        }
      });
      var_declaration_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          console.log(line, "-> var_declartion");
        }
      });
      assign_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          console.log(line, "-> assign_stmt");
        }
      });
      input_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          console.log(line, "-> input_stmt_keywords");
        }
      });
      output_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          console.log(line, "-> output_stmt_keywords");
        }
      });
      loop_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          console.log(line, "-> loop_stmt_keywords");
        }
      });
      if_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          console.log(line, "-> if_stmt_keywords");
        }
      });
    });
  });
})
