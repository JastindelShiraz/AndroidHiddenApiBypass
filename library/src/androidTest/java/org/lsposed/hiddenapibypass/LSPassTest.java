package org.lsposed.hiddenapibypass;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.ClipDrawable;
import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import dalvik.system.VMRuntime;

@SuppressWarnings("JavaReflectionMemberAccess")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4.class)
public class LSPassTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void AgetDeclaredMethods() {
        Method[] methods = LSPass.getDeclaredMethods(VMRuntime.class);
        Optional<Method> getRuntime = Arrays.stream(methods).filter(it -> it.getName().equals("getRuntime")).findFirst();
        assertTrue(getRuntime.isPresent());
        Optional<Method> setHiddenApiExemptions = Arrays.stream(methods).filter(it -> it.getName().equals("setHiddenApiExemptions")).findFirst();
        assertTrue(setHiddenApiExemptions.isPresent());
    }

    @Test(expected = NoSuchMethodException.class)
    public void BusesNonSdkApiIsHiddenApi() throws NoSuchMethodException {
        ApplicationInfo.class.getMethod("getHiddenApiEnforcementPolicy");
    }

    @Test(expected = NoSuchMethodException.class)
    public void CsetHiddenApiExemptionsIsHiddenApi() throws NoSuchMethodException {
        VMRuntime.class.getMethod("setHiddenApiExemptions", String[].class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void DnewClipDrawableIsHiddenApi() throws NoSuchMethodException {
        ClipDrawable.class.getDeclaredConstructor();
    }

    @Test(expected = NoSuchFieldException.class)
    public void ElongVersionCodeIsHiddenApi() throws NoSuchFieldException {
        ApplicationInfo.class.getDeclaredField("longVersionCode");
    }

    @Test(expected = NoSuchFieldException.class)
    public void FHiddenApiEnforcementDefaultIsHiddenApi() throws NoSuchFieldException {
        ApplicationInfo.class.getDeclaredField("HIDDEN_API_ENFORCEMENT_DEFAULT");
    }

    @Test
    public void GtestGetInstanceFields() {
        assertTrue(Arrays.stream(LSPass.getDeclaredFields(ApplicationInfo.class)).anyMatch(i -> i.getName().equals("longVersionCode")));
    }

    @Test
    public void HtestGetStaticFields() {
        assertTrue(Arrays.stream(LSPass.getDeclaredFields(ApplicationInfo.class)).anyMatch(i -> i.getName().equals("HIDDEN_API_ENFORCEMENT_DEFAULT")));
    }

    @Test
    public void IinvokeNonSdkApiWithoutExemption() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertNotEquals(LSPass.getDeclaredMethod(ApplicationInfo.class, "getHiddenApiEnforcementPolicy"), null);
        LSPass.invoke(ApplicationInfo.class, new ApplicationInfo(), "getHiddenApiEnforcementPolicy");
    }

    @Test
    public void JnewClipDrawableWithoutExemption() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        assertNotEquals(LSPass.getDeclaredConstructor(ClipDrawable.class), null);
        Object instance = LSPass.newInstance(ClipDrawable.class);
        assertSame(instance.getClass(), ClipDrawable.class);
    }

    @Test
    public void KgetAllMethodsWithoutExemption() {
        assertTrue(Arrays.stream(LSPass.getDeclaredMethods(ApplicationInfo.class)).anyMatch(e -> e.getName().equals("getHiddenApiEnforcementPolicy")));
    }

    @Test
    public void LsetHiddenApiExemptions() throws NoSuchMethodException, NoSuchFieldException {
        assertTrue(LSPass.setHiddenApiExemptions("Landroid/content/pm/ApplicationInfo;"));
        ApplicationInfo.class.getMethod("getHiddenApiEnforcementPolicy");
        ApplicationInfo.class.getDeclaredField("longVersionCode");
        ApplicationInfo.class.getDeclaredField("HIDDEN_API_ENFORCEMENT_DEFAULT");
    }

    @Test
    public void MclearHiddenApiExemptions() throws NoSuchMethodException {
        exception.expect(NoSuchMethodException.class);
        exception.expectMessage(containsString("setHiddenApiExemptions"));
        assertTrue(LSPass.setHiddenApiExemptions("L"));
        ApplicationInfo.class.getMethod("getHiddenApiEnforcementPolicy");
        assertTrue(LSPass.clearHiddenApiExemptions());
        VMRuntime.class.getMethod("setHiddenApiExemptions", String[].class);
    }

    @Test
    public void NaddHiddenApiExemptionsTest() throws NoSuchMethodException {
        assertTrue(LSPass.addHiddenApiExemptions("Landroid/content/pm/ApplicationInfo;"));
        ApplicationInfo.class.getMethod("getHiddenApiEnforcementPolicy");
        assertTrue(LSPass.addHiddenApiExemptions("Ldalvik/system/VMRuntime;"));
        VMRuntime.class.getMethod("setHiddenApiExemptions", String[].class);
    }

}
