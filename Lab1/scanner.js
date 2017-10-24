
$(document).ready(function(){

  user_type_definition_keywords = [/^define /];
  var_declaration_keywords = [/^int /, /^real /];
  assign_stmt_keywords = [/=/];
  input_stmt_keywords = [/^input\(/];
  output_stmt_keywords = [/^output\(/];
  loop_stmt_keywords = [/while\(/];
  if_stmt_keywords = [/if\(/];
  logical_expression_keywords = [/==|!=|<=|>=|<|>/]
  arithm_operators = ["+", "-", "*", "/", "%"];
  code = {"id": "0",
    "define": "1",
    "(": "2",
    "real": "3",
    ")": "4",
    ";": "5",
    "int": "6",
    "input": "7",
    ",": "8",
    "=": "9",
    "*": "10",
    "+": "11",
    "-": "12",
    "/": "13",
    "%": "14",
    "output": "15",
    "while": "16",
    "==": "17",
    "!=": "18",
    "<=": "19",
    ">=": "20",
    "<": "21",
    ">": "22",
    "begin:": "23",
    "end": "24",
    "if": "25",
    ".": "26"
  };
  pif = {};
  st = {};
  crt_mem_pos = 1000;
  crt_code_pos = 27;
  init_code();


  function clear_empty_elements(input_lines){
    input_lines.forEach(function(item, index){
      if(item === "" || item === " "){
        input_lines.splice(index, 1);
      }
    });
  }

  function init_code(){
    code_table = $("#code");
    for(var key in code){
      code_table.append("<tr><td>" + key + "</td><td>" + code[key] + "</td></tr>");
    }
  }

  function exists(table, item){
    for(var key in table){
      if(item === key){
        return true;
      }
    }
    return false;
  }


  $(".run-btn").on("click", function(){
    var input_code = $(".code-input").val();
    var input_lines = input_code.split(/;|\n/);
    clear_empty_elements(input_lines);
    pif_table = $("#pif-table");
    symbol_table = $("#symbol-table");
    code_table = $("#code");

    input_lines.forEach(function(line, index){
      user_type_definition_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          words = line.split(/ |\(|\)|\( | \)/);
          clear_empty_elements(words);
          var_declaration_keywords.push(new RegExp("^" + words[1]))
          code[words[1]] = String(crt_code_pos);
          code_table.append("<tr><td>" + words[1] + "</td><td>" + crt_code_pos + "</td></tr>");
          crt_code_pos+=1;

          pif_table.append("<tr><td>" + code[words[0].trim()] + "</td><td>" + "-" + "</td></tr>");
          pif_table.append("<tr><td>" + code[words[1].trim()] + "</td><td>" + "-" + "</td></tr>");
          pif_table.append("<tr><td>" + code["("] + "</td><td>" + "-" + "</td></tr>");
          for(var i=2; i< words.length; i+=2){
            pif_table.append("<tr><td>" + code[words[i].trim()] + "</td><td>" + "-" + "</td></tr>");
          }
          for(var i=3; i< words.length; i+=2){
            if(exists(st, words[i])){
              pif_table.append("<tr><td>" + "0" + "</td><td>" + st[words[i]] + "</td></tr>");
            }
            else{
              st[words[i]] = crt_mem_pos;
              crt_mem_pos += 1;
              pif_table.append("<tr><td>" + "0" + "</td><td>" + st[words[i]] + "</td></tr>");
            }
            if(i!==words.length - 1){
              pif_table.append("<tr><td>" + code[","] + "</td><td>" + "-" + "</td></tr>");
            }
          }
          pif_table.append("<tr><td>" + code[")"] + "</td><td>" + "-" + "</td></tr>");
          pif_table.append("<tr><td>" + code[";"] + "</td><td>" + "-" + "</td></tr>");
        }
      });
      var_declaration_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          words = line.split(/ |,/)
          clear_empty_elements(words);
          console.log(words);
          words.forEach(function(item, index){
            var is_keyword = false;
            for(var key in code){
              if(item === key){
                is_keyword = true;
                break;
              }
            }
            if(is_keyword){
              pif_table.append("<tr><td>" + code[key] + "</td><td>-</td></tr>");
            }
            else{
              if(!exists(st, item)){
                st[item] = crt_mem_pos;
                crt_mem_pos += 1;
              }
              pif_table.append("<tr><td> 0 </td><td>" + st[item] + "</td></tr>")
            }
          });
        }
      });
      assign_stmt_keywords.forEach(function(keyword, index){
        logical_expression_keywords.forEach(function(keyword2, index){
          if(line.search(keyword) !== -1 && line.search(keyword2) === -1){
            words = line.split(/=/);
            clear_empty_elements(words);
            if(!exists(st, words[0].trim())){
              console.log("Error! The variable does not exist");
            }
            else{
              pif_table.append("<tr><td>" + "0" + "</td><td>" + String(st[words[0].trim()]) + "</td></tr>");
              pif_table.append("<tr><td>" + code["="] + "</td><td>-</td></tr>");
            }
            var partialRes = "";
            for(var i=1;i<words[1].length;i++){
              if(arithm_operators.indexOf(words[1][i]) !== -1){
                if(!exists(st, partialRes.trim())){
                  st[partialRes] = crt_mem_pos;
                  crt_mem_pos += 1;
                }
                pif_table.append("<tr><td>" + "0" + "</td><td>" + st[partialRes] + "</td></tr>");
                pif_table.append("<tr><td>" + code[words[1][i]] + "</td><td>-</td></tr>");
                partialRes = "";
              }
              if(words[1][i] === "."){
                pif_table.append("<tr><td>" + "0" + "</td><td>" + st[partialRes] + "</td></tr>");
                pif_table.append("<tr><td>" + code[words[1][i]] + "</td><td>-</td></tr>");
                partialRes = "";
              }
              else{
                partialRes += words[1][i];
              }
            }
            if(!exists(st, partialRes.trim())){
              st[partialRes] = crt_mem_pos;
              crt_mem_pos += 1;
            }
            pif_table.append("<tr><td>" + "0" + "</td><td>" + st[partialRes] + "</td></tr>");
            pif_table.append("<tr><td>" + code[";"] + "</td><td>-</td></tr>");
            console.log(st);
          }
        });
      });
      input_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          words = line.split(/\(|\)/);
          clear_empty_elements(words);
          words.forEach(function(item, index){
            var is_keyword = false;
            for(var key in code){
              if(item === key){
                is_keyword = true;
                break;
              }
            }
            if(is_keyword){
              pif_table.append("<tr><td>" + code[key] + "</td><td>-</td></tr>");
            }
            else{
              if(item.indexOf(".") !== -1){
                split_item = item.split(/\./);
                if(exists(st, split_item[0].trim())){
                  console.log(st);
                  var x = st[split_item[0].trim()];
                  console.log("<tr><td> 0 </td><td>" + x + "</td></tr>");
                  pif_table[0].innerHTML += "<tr><td> 0 </td><td>" + x + "</td></tr>";
                }
                else{
                  console.log("Error! The variable does not exist");
                }
                pif_table.append("<tr><td>" + code["."] + "</td><td>-</td></tr>");
                if(exists(st, split_item[1])){
                  pif_table.append("<tr><td> 0 </td><td>" + st[split_item[1].trim()] + "</td></tr>")
                }
                else{
                  console.log("Error! The variable does not exist");
                }
              }
              else {
                if(!exists(st, item.trim())){
                  console.log("Error! The variable does not exist");
                }
                else{
                  pif_table.append("<tr><td> 0 </td><td>" + st[item.trim()] + "</td></tr>");
                }
              }
            }
          });
          pif_table.append("<tr><td>" + code[";"] + "</td><td>-</td></tr>");
        }
      });
      output_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          words = line.split(/\(|\)/);
          clear_empty_elements(words);
          words.forEach(function(item, index){
            var is_keyword = false;
            for(var key in code){
              if(item === key){
                is_keyword = true;
                break;
              }
            }
            if(is_keyword){
              pif_table.append("<tr><td>" + code[key] + "</td><td>-</td></tr>");
            }
            else{
              var exists = false;
              for(var key in st){
                if(item == key){
                  exists = true;
                }
              }
              if(!exists){
                st[item] = crt_mem_pos;
                crt_mem_pos += 1;
              }
              pif_table.append("<tr><td> 0 </td><td>" + st[item] + "</td></tr>")
            }
          });
          pif_table.append("<tr><td>" + code[";"] + "</td><td>-</td></tr>");
        }
      });
      loop_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          words = line.split(/\(|\)/);
          clear_empty_elements(words);
          console.log(words);
        }
      });
      if_stmt_keywords.forEach(function(keyword, index){
        if(line.search(keyword) !== -1){
          words = line.split(/\(|\)/);
          clear_empty_elements(words);
          console.log(words);
        }
      });
    });
  });
})
