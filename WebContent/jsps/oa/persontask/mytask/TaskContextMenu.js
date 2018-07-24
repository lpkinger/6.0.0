//extended context menu, color picker added
Ext.define('MyApp.TaskContextMenu', {
    extend: 'Gnt.plugin.TaskContextMenu',
    constructor : function(){
        this.texts.changeColor = 'Change task color';

        this.callParent(arguments);

    },
    createMenuItems : function() {
        var items = this.callParent(arguments);

        return [{
            text: this.texts.changeColor,
            menu: {
                showSeparator: false,
                items: [
                    Ext.create('Ext.ColorPalette', {
                        listeners: {
                            select: function(cp, color){        
                                this.rec.set('TaskColor', color);
                            },
                            scope: this
                        }
                    })
                ]
            }
        }].concat(items);
    }
});