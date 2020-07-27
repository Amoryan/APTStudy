package com.ppdai.api;

import com.ppdai.core.ISyringe;

/**
 * @author sunshine big boy
 *
 * <pre>
 *      talk is cheap, show me the code
 * </pre>
 */
public class PPdaiHelper {

    public static void inject(Object target) {
        String className = target.getClass().getName() + "$$ARouter$$Autowired";
        try {
            ISyringe iSyringe = (ISyringe) Class.forName(className).getConstructor().newInstance();
            iSyringe.inject(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
