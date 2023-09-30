/*!
 * JavaScript implementation for the client side of the Multiple Subtasks plugin for the configuration page.
 */
(function(MultipleSubtasksConfiguration, $, undefined) {

   // load the configuration and apply all settings to the respective fields
    const getConfiguration = async function () {
        console.log('Loading configuration...');
        const response = await fetch(AJS.contextPath() + '/rest/multiple-subtasks/1.0/configuration');
        const configuration = await response.json();

        if (configuration.length > 0) {
            configuration.forEach(function(config) {
                console.log(config);
                if (AJS.$('#' + config.key).length) {
                    AJS.$('#' + config.key).val(config.value);
                }
            });
        }
    }

    // save the changed configuration and reload all settings
    const saveConfiguration = async function (key, value) {
        const response = await fetch(AJS.contextPath() + '/rest/multiple-subtasks/1.0/configuration/' + key, {
            method: 'POST',
            body: value,
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (response.ok) {
            AJS.flag({
              type: 'success',
              body: AJS.I18n.getText('multiplesubtasks.configuration.save.success'),
              close: 'auto'
            });
        }
        await getConfiguration();
    }

    AJS.$(document).ready(function () {
        // show all configured settings
        getConfiguration();

        // register any changes to be send to the server
        AJS.$(".multiple-subtasks-config").change(function() {
            const key = $(this).attr('name');
            const value = $(this).val();
            console.log('Updating value for ' + key + ' with value ' + value);
            saveConfiguration(key, value);
        });
    });

}(window.MultipleSubtasksConfiguration = window.MultipleSubtasksConfiguration || {}, jQuery));
