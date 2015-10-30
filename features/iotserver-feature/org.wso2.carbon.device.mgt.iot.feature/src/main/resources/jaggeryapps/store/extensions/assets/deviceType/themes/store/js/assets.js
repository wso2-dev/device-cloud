$('.ast-name.truncate').each(function(){
    if($(this).attr('title') == 'virtual_firealarm'){
        var parent = $(this).closest('.ctrl-wr-asset');
        parent.hide();
        $('.try-device-image').attr('src', parent.find('.img-responsive').attr('src'));
        $('.try-device-text .btn').attr('href', parent.find('.ast-img ').attr('href'));
    }
});