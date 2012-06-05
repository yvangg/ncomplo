
$(function() {

    
    /*
     * =======================================
     * NAMESPACE CREATION
     * =======================================
     */
    

    if (!$.org) {
        $.org = {};
    }
    if (!$.org.eleventhlabs) {
        $.org.eleventhlabs = {};
    }
    if ($.org.eleventhlabs.ncomplo) {
        throw new Error('org.eleventhlabs.ncomplo already exists!');
    }
  
    $.org.eleventhlabs.ncomplo = {};
  

    
    /*
     * =======================================
     * GLOBAL VARIABLES (ncomplo-object scope)
     * =======================================
     */
    
    
    $.org.eleventhlabs.ncomplo.lang = {};
    $.org.eleventhlabs.ncomplo.lang.prototypes = {};
    $.org.eleventhlabs.ncomplo.lang.LANG_INPUT_CLASS = 'langInput';
    $.org.eleventhlabs.ncomplo.lang.VALUE_INPUT_CLASS = 'valueInput';
    $.org.eleventhlabs.ncomplo.lang.LANG_BLOCK_NEW_CLASS_SUFFIX = '-lang-new';
    $.org.eleventhlabs.ncomplo.lang.LANG_BLOCK_EXISTING_CLASS_SUFFIX = '-lang-existing';
    $.org.eleventhlabs.ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX = '-lang-prototype';
        
    
    
    /*
     * =======================================
     * FUNCTIONS
     * =======================================
     */
    
    
    $.org.eleventhlabs.ncomplo.initialize =
        function() {
            
            var ncomplo = this;

            /*
             * --------------------------
             * Initialize lang prototypes
             * --------------------------
             */
        
            // Retrieves all lang-oriented prototype divs, which
            // "id" attribute should end with "-lang"
            var prototypes = $('[id$=' + ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX + ']');
            
            prototypes.each(
                    function() {
                        var prototype = this;
                        ncomplo.lang.prototypes[this.id] = 
                            {
                                name : 
                                    prototype.id.substring(0,this.id.indexOf(ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX)),
                                index : 
                                    (function() {
                                        var name = 
                                            prototype.id.substring(0,prototype.id.indexOf(ncomplo.lang.LANG_BLOCK_PROTOTYPE_ID_SUFFIX));
                                        var existingClass =
                                            name + ncomplo.lang.LANG_BLOCK_EXISTING_CLASS_SUFFIX;
                                        return $(prototype).parent().children().filter('.' + existingClass).size();
                                    })(),
                                element: $(this)
                            };
                        $(this).remove();
                    });

            
        };
    
        
        
    $.org.eleventhlabs.ncomplo.lang.add =
        function(langElementId) {
        
            var defaultLangElement = $('#'+langElementId);
            var prototypeObject = this.prototypes[langElementId + this.LANG_BLOCK_PROTOTYPE_ID_SUFFIX];
            
            var newLangElement = prototypeObject.element.clone();
            newLangElement.attr('id',null);
            newLangElement.addClass(prototypeObject.name + this.LANG_BLOCK_NEW_CLASS_SUFFIX);
            
            var langInput = newLangElement.children().filter('.' + this.LANG_INPUT_CLASS);
            var valueInput = newLangElement.children().filter('.' + this.VALUE_INPUT_CLASS);

            langInput.attr(
                    'name',
                    langInput.attr('name').replace('$index$',prototypeObject.index));
            valueInput.attr(
                    'name',
                    valueInput.attr('name').replace('$index$',prototypeObject.index));

            langInput.attr(
                    'id',
                    langInput.attr('id').replace('$index$',prototypeObject.index));
            valueInput.attr(
                    'id',
                    valueInput.attr('id').replace('$index$',prototypeObject.index));
            
            prototypeObject.index++;
            
            langInput.css('background-color','red');
            valueInput.css('background-color','blue');

            
            var existingElements =
                defaultLangElement.parent().children().filter('.' + prototypeObject.name + this.LANG_BLOCK_EXISTING_CLASS_SUFFIX);
            var newElements =
                defaultLangElement.parent().children().filter('.' + prototypeObject.name + this.LANG_BLOCK_NEW_CLASS_SUFFIX);

            if (newElements.size() > 0) {
                newElements.last().after(newLangElement);
            } else if (existingElements.size() > 0) {
                existingElements.last().after(newLangElement);
            } else {
                defaultLangElement.after(newLangElement);
            }
            
        };
        
        
        
        
    /*
     * =======================================
     * INITIALIZATION
     * =======================================
     */
        
    $.org.eleventhlabs.ncomplo.initialize();
    
    
});
