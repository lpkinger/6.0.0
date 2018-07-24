/**
 * @author Steven Roussey
 * 
 * @ usage:
 *     {
 *	       xtype:'combo',
 *	       store:['Reader','Participant','Moderator','SuperUser']
 *      }
 * Or
 *     {
 *	       xtype:'combo',
 *	       store:[['r','Reader'],['p','Participant'],['m','Moderator'],['s','SuperUser']]
 *      }
 */

Ext.ux.ComboBox = function(config){
	if (config.store && typeof config.store !='string' && config.store.length>1)
	{
		if (typeof config.store[0] !='string' && config.store[0].length>1)
		{
			config.store = new Ext.data.SimpleStore({
			    fields: ['value','text'],
			    data : config.store
			});
	        config.valueField = 'value';
            config.displayField = 'text';
		}
		else
		{
			var store=[];
			for (var i=0,len=config.store.length;i<len;i++)
				store[i]=[config.store[i]];
			config.store = new Ext.data.SimpleStore({
			    fields: ['text'],
			    data : store
			});
	        config.valueField = 'text';
            config.displayField = 'text';
		}
		config.mode = 'local';
	}
    Ext.ux.ComboBox.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.ComboBox,Ext.form.ComboBox,{
	});
Ext.reg('combo',Ext.ux.ComboBox);