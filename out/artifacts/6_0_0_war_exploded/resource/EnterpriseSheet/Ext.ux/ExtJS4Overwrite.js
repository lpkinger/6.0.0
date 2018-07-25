/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) CubeDrive Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
if(!Ext.isIE){
	/*		 
	 * EXT 4.2.1 or EXT 4.2.2 can not set isIE rightly in IE11 and edge, we need check again
	 */
	var userAgent = navigator.userAgent;
				
	if(/(Edge\/)|(MSIE)/gi.test(userAgent) || /(Trident\/)/gi.test(navigator.appVersion)) {
		Ext.isIE = true;
		Ext.isChrome = false;
		Ext.isSafari = false;
		Ext.isWebKit = false;
		Ext.isGecko = false;
		Ext.isOpera = false;
	}
}

if(Ext.form.field.VTypes){
	Ext.apply(Ext.form.field.VTypes, {
        varname:  function(v) {
            return /^(([a-z_]+[a-z_0-9]*)|([a-z_]+[a-z_0-9.]*[a-z_]+))$/.test(v);
        },
        varnameText: 'Must be composed by letter, number, _ or .'
    });
}  

Ext.override(Ext.menu.Menu, {
    onMouseLeave: function(e) {
	    var me = this;
	
	    var visibleSubmenu = false;
	    me.items.each(function(item) { 
	        if(item.menu && item.menu.isVisible()) { 
	            visibleSubmenu = true;
	        }
	    })
	    if(visibleSubmenu) {
	        return;
	    }
	
	    me.deactivateActiveItem();
	
	    if (me.disabled) {
	        return;
	    }
	
	    me.fireEvent('mouseleave', me, e);
    }
});

Ext.override(Ext.button.Button, {
	renderTpl: [
	    '<span id="{id}-btnWrap" class="{baseCls}-wrap',
	    	'<tpl if="splitCls"> {splitCls}</tpl>',
	    	'{childElCls}" unselectable="on">',
            '<span id="{id}-btnEl" class="{baseCls}-button">',
                '<span id="{id}-btnInnerEl" class="{baseCls}-inner {innerCls}',
                    '{childElCls}" unselectable="on">',
                    '{text}',
                '</span>',
                '<span role="img" id="{id}-btnIconEl" class="{baseCls}-icon-el {iconCls}',
                    '{childElCls} {glyphCls}" unselectable="on" style="',
                    '<tpl if="iconUrl">background-image:url({iconUrl});</tpl>',
                    '<tpl if="glyph && glyphFontFamily">font-family:{glyphFontFamily};</tpl>">',
                    '<tpl if="glyph">&#{glyph};</tpl><tpl if="iconCls || iconUrl"><tpl if="needBlank">&#160;</tpl></tpl>',
                '</span>',
            '</span>',
        '</span>',
        // if "closable" (tab) add a close element icon
        '<tpl if="closable">',
            '<span id="{id}-closeEl" class="{baseCls}-close-btn" title="{closeText}" tabIndex="0"></span>',
        '</tpl>'	                
   ],
   
   getTemplateArgs: function() {
       var me = this,
           glyph = me.glyph,
           glyphFontFamily = Ext._glyphFontFamily,
           glyphParts;

       if (typeof glyph === 'string') {
           glyphParts = glyph.split('@');
           glyph = glyphParts[0];
           glyphFontFamily = glyphParts[1];
       }

       return {
           innerCls : me.getInnerCls(),
           splitCls : me.getSplitCls(),
           iconUrl  : me.icon,
           iconCls  : me.iconCls,
           glyph: glyph,
           glyphCls: glyph ? me.glyphCls : '', 
           glyphFontFamily: glyphFontFamily,
           needBlank: !(me.iconCls && -1 !== me.iconCls.indexOf('fa fa-')), 
           text     : me.text || '&#160;'
       };
   }
});