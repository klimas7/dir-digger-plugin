'use strict';
jQuery.noConflict();

var DirDigger = DirDigger || (function($) {
        var instance = {};

        function Digger(selections, name, url) {
            this.selections = selections;
            this.name = name;
            this.url = url;
            this.initChangeHandler();
        }

        Digger.prototype.getSelections = function() {
            return this.selections;
        }

        Digger.prototype.getName = function() {
            return this.name;
        }

        Digger.prototype.getUrl = function() {
            return this.url;
        }

        Digger.prototype.initChangeHandler = function() {
             var _self = this;

             for (let lvl = 0; lvl < _self.getSelections().length - 1; lvl++) {
                var selection = _self.getSelections()[lvl];

                selection.change(function() {
                    var values = [];
                    for (let i=0; i <= lvl; i++) {
                        values.push(_self.getSelections()[i].val())
                    }

                    let jsonValues = values.toJSON()
                    $.ajax({
                        url: _self.getUrl()+'/fillValueItems?parameterName='+_self.getName()+'&jsonFileNames='+jsonValues,
                        level: lvl
                    }).done(function(listModelBox) {

                        let selection = _self.getSelections()[this.level+1]
                        selection.find('option').remove().end();
                        $.each(listModelBox.values, function(j, value) {
                            selection.append($('<option>', {
                                value: value.value,
                                text: value.value
                            }));
                        });
                        selection.trigger("change");
                    });
                });
             }
        }

        instance.Digger = Digger;
        return instance;
})(jQuery);
