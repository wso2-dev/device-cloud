var deviceId, deviceType;
$('select.select2').select2({
    placeholder: 'Select..'
});

$('select.select2[multiple=multiple]').select2({
    placeholder: 'Select..',
    tags: true
});

var stepperRegistry = {},
    hiddenOperation = '.wr-hidden-operations-content > div',
    advanceOperation = '.wr-advance-operations';

function initStepper(selector) {
    $(selector).click(function () {
        var nextStep = $(this).data("next");
        var currentStep = $(this).data("current");
        var isBack = $(this).data("back");
        if (!isBack) {
            var action = stepperRegistry[currentStep];
            if (action) {
                action(this);
            }
        }
        if (!nextStep) {
            var direct = $(this).data("direct");
            window.location.href = direct;
        }
        $(".itm-wiz").each(function () {
            var step = $(this).data("step");
            if (step == nextStep) {
                $(this).addClass("itm-wiz-current");
            } else {
                $(this).removeClass("itm-wiz-current");
            }
        });
        $(".wr-wizard").html($(".wr-steps").html());
        $("." + nextStep).removeClass("hidden");
        $("." + currentStep).addClass("hidden");

    });
}

function showAdvanceOperation(operation, button) {
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
}

var policy = {};
var configuredProfiles = [];

function savePolicy() {

    var payload = {
        policyName: policy.policyName,
        compliance: policy.selectedAction,
        ownershipType: policy.selectedOwnership,
        profile: {
            profileName: policy.policyName,
            deviceType: {
                id: policy.devicetypeId,
                name: policy.devicetype
            },
            policyDefinition: policy.policyDefinition,
            policyDescription: policy.policyDescription
        }
    };

    invokerUtil.post("../../../apis/policies/add", payload, function (data, txtStatus, jqxhr) {
        $(".policy-message").removeClass("hidden");
        $(".add-policy").addClass("hidden");
        setTimeout(function () {
            window.location = "../policies";
        }, 1500);
    }, function (err) {
        console.log(err);
    });
}

$(document).ready(function () {
    deviceId = getQueryParams('deviceId');
    deviceType = getQueryParams('deviceType');

    initStepper(".wizard-stepper");
    $(".wr-wizard").html($(".wr-steps").html());

    if (deviceId && deviceType){
        policy.devicetype = deviceType;
        policy.deviceId = deviceId;
        $('.policy-desc').removeClass("hidden");
        $('.policy-devicetype').addClass("hidden");

        var deviceType = policy.devicetype;
        var hiddenOperationsByDeviceType = $("#hidden-operations-" + deviceType);
        var hiddenOperationsByDeviceTypeCacheKey = deviceType + "HiddenOperations";
        var hiddenOperationsByDeviceTypeSrc = hiddenOperationsByDeviceType.attr("src");
        setTimeout(
            function () {
                $.template(hiddenOperationsByDeviceTypeCacheKey, hiddenOperationsByDeviceTypeSrc, function (template) {
                    var content = template();
                    $(".wr-advance-operations").html(content);
                    $(".wr-advance-operations li.grouped-input").each(function () {
                        updateGroupedInputVisibility(this);
                    });
                });
            },
            250 // time delayed for the execution of above function, 250 milliseconds
        );
    }

    $("input[type='radio'].user-select-radio").change(function () {
        $('.user-select').hide();
        $('#' + $(this).val()).show();
    });
    //Adds an event listener to swithc
    $(advanceOperation).on("click", ".wr-input-control.switch", function (evt) {
        var operation = $(this).parents(".operation-data").data("operation");
        //prevents event bubbling by figuring out what element it's being called from
        if (evt.target.tagName == "INPUT") {
            if (!$(this).hasClass('collapsed')) {
                configuredProfiles.push(operation);
            } else {
                //splicing the array if operation is present
                var index = jQuery.inArray(operation, configuredProfiles);
                if (index != -1) {
                    configuredProfiles.splice(index, 1);
                }
            }
            console.log(configuredProfiles);
        }

    });

    stepperRegistry['policy-devicetype'] = function (actionButton) {
        policy.devicetype = $(actionButton).data("devicetype");
        policy.devicetypeId = $(actionButton).data("devicetype-id");

        var deviceType = policy.devicetype;
        var hiddenOperationsByDeviceType = $("#hidden-operations-" + deviceType);
        var hiddenOperationsByDeviceTypeCacheKey = deviceType + "HiddenOperations";
        var hiddenOperationsByDeviceTypeSrc = hiddenOperationsByDeviceType.attr("src");
        setTimeout(
            function () {
                $.template(hiddenOperationsByDeviceTypeCacheKey, hiddenOperationsByDeviceTypeSrc, function (template) {
                    var content = template();
                    $(".wr-advance-operations").html(content);
                    $(".wr-advance-operations li.grouped-input").each(function () {
                        updateGroupedInputVisibility(this);
                    });
                });
            },
            250 // time delayed for the execution of above function, 250 milliseconds
        );
    };


    /**
     * Method to update the visibility of grouped input.
     * @param domElement HTML grouped-input element with class name "grouped-input"
     */
    var updateGroupedInputVisibility = function (domElement) {
        if ($(".parent-input:first", domElement).is(":checked")) {
            if ($(".grouped-child-input:first", domElement).hasClass("disabled")) {
                $(".grouped-child-input:first", domElement).removeClass("disabled");
            }
            $(".child-input", domElement).each(function () {
                $(this).prop('disabled', false);
            });
        } else {
            if (!$(".grouped-child-input:first", domElement).hasClass("disabled")) {
                $(".grouped-child-input:first", domElement).addClass("disabled");
            }
            $(".child-input", domElement).each(function () {
                $(this).prop('disabled', true);
            });
        }
    };

    stepperRegistry['policy-config-profile'] = function (actionButton) {
        if(policy.devicetype == "virtual_firealarm"){
            var timeInterval = $("#time-interval").val();
            var triggerTemp = $("#trigger-temp").val();
            if(timeInterval == "" || timeInterval < 0){
                timeInterval = 30;
            }
            if(triggerTemp == "" ||triggerTemp < 0){
                triggerTemp = 50;
            }
            window.queryEditor.setValue("define stream fireAlarmEventStream (deviceID string, temp int)\n" +
            "from fireAlarmEventStream#window.time("+timeInterval+" sec)\n" +
            "select deviceID, max(temp) as maxValue\n"+
            "group by deviceID\n"+
            "insert into analyzeStream for expired-events;\n" +
            "from analyzeStream[maxValue < "+triggerTemp+"]\n"+
            "select maxValue\n" +
            "insert into bulbOnStream;\n" +
            "from fireAlarmEventStream[temp > "+triggerTemp+"]\n" +
            "select deviceID, temp\n" +
            "insert into bulbOffStream;\n");
            window.queryEditor.refresh();
        }
    };

    stepperRegistry['policy-content'] = function (actionButton) {
        policy.policyDefinition = window.queryEditor.getValue();
        policy.policyName = $("#policy-name-input").val();
        policy.policyDescription = $("#policy-description-input").val();
        //All data is collected. Policy can now be created.
        savePolicy();
    };

    $(".uu").click(function () {
        var policyName = $("#policy-name-input").val();
        var selectedProfiles = $("#profile-input").find(":selected");
        var selectedProfileId = selectedProfiles.data("id");
        var selectedUserRoles = $("#user-roles-input").val();
        var selectedUsers = $("#users-input").val();
        var selectedAction = $("#action-input").val();
    });

    var mime = MIME_TYPE_SIDDHI_QL;

    // get mime type
    if (window.location.href.indexOf('mime=') > -1) {
        mime = window.location.href.substr(window.location.href.indexOf('mime=') + 5);
    }

    window.queryEditor = CodeMirror.fromTextArea(document.getElementById('policy-definition-input'), {
        mode: mime,
        indentWithTabs: true,
        smartIndent: true,
        lineNumbers: true,
        matchBrackets: true,
        autofocus: true,
        extraKeys: {
            "Shift-2": function (cm) {
                insertStr(cm, cm.getCursor(), '@');
                CodeMirror.showHint(cm, getAnnotationHints);
            },
            "Ctrl-Space": "autocomplete"
        }
    });

});

function getQueryParams(key) {
    var qs = document.location.search.split('+').join(' ');

    var params = {},
            tokens,
            re = /[?&]?([^=]+)=([^&]*)/g;

    while (tokens = re.exec(qs)) {
        params[decodeURIComponent(tokens[1])] = decodeURIComponent(tokens[2]);
    }

    return params[key];
}