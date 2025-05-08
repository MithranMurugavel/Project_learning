
function toggle(){
    var val1=document.getElementById("input1").value.trim();
    var val2=document.getElementById("input2").value.trim();

    if(isNaN(val1)){
        val1=Number(0);
    }
    else if(isNaN(val2)){
        val2=Number(0);
    }
    var value=parseInt(val1)+parseInt(val2);
    const result=document.getElementById("result");

    result.innerHTML = `The Addition of ${val1} and  ${val2} is ${value}`;
}