/**
 * Created by HZ-HAN on 14/06/11.
 */


$(".boxChecked").click(
    function () {
       var select = $(this).get(0)
       if(select.checked == true){
           $('.page_btn').removeClass('disabled').addClass('success')
       }else{
           $('.page_btn').removeClass('success').addClass('disabled')
       }
       $("input[type='checkbox']").each(function(i){
            $(this).attr('checked',select.checked);
       });
    }
);