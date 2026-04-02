package com.chinaunicom.edu.agent.core.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 动作信息
 */
@Data
public class Action {
    
    /**
     * 动作类型 (open_url, show_detail, start_exercise, submit_homework等)
     */
    private String type;
    
    /**
     * 动作名称
     */
    private String name;
    
    /**
     * 显示文本
     */
    private String label;
    
    /**
     * 动作参数
     */
    private Map<String, Object> params;
    
    /**
     * 按钮样式 (primary, secondary, danger等)
     */
    private String style;
    
    public Action() {
        this.params = new HashMap<>();
    }
    
    public Action(String type, String name, String label) {
        this();
        this.type = type;
        this.name = name;
        this.label = label;
    }
    
    public static ActionBuilder builder() {
        return new ActionBuilder();
    }
    
    public static class ActionBuilder {
        private Action action = new Action();
        
        public ActionBuilder type(String type) {
            action.setType(type);
            return this;
        }
        
        public ActionBuilder name(String name) {
            action.setName(name);
            return this;
        }
        
        public ActionBuilder label(String label) {
            action.setLabel(label);
            return this;
        }
        
        public ActionBuilder param(String key, Object value) {
            action.getParams().put(key, value);
            return this;
        }
        
        public ActionBuilder style(String style) {
            action.setStyle(style);
            return this;
        }
        
        public Action build() {
            return action;
        }
    }
}
