'use strict';
jQuery.noConflict();

var DirDigger = DirDigger || (function($) {
        var instance = {};

        function Digger(selections) {
            this.selections = selections;
        }

        Digger.prototype.getSelections = function() {
            return this.selections;
        }


        instance.Digger = Digger;
        return instance;
})(jQuery);
