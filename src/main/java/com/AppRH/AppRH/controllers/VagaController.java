package com.AppRH.AppRH.controllers;

import com.AppRH.AppRH.models.Candidatos;
import com.AppRH.AppRH.models.Vaga;
import com.AppRH.AppRH.repository.CandidatoRepository;
import com.AppRH.AppRH.repository.VagaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
public class VagaController {

    private VagaRepository vr;
    private CandidatoRepository cr;

    @RequestMapping(value = "/cadastrarVaga", method = RequestMethod.GET)
    public String form(){
        return "vaga/formVaga";
    }

    // SALVAR VAGA
    @RequestMapping(value = "/cadastrarVaga", method = RequestMethod.POST)
    public String form(@Valid Vaga vaga, BindingResult result, RedirectAttributes attributes){

        if(result.hasErrors()){
            attributes.addFlashAttribute("mensagem", "Verifica os campos...");
            return "redirect:/cadastrarVaga";
        }
        vr.save(vaga);
        attributes.addFlashAttribute("mensagem", "Vaga cadastrado com sucesso!");
        return "redirect:/cadastrarVaga";
    }

    // LISTAR VAGAS
    @RequestMapping("/vagas")
    public ModelAndView listarVagas(){
        ModelAndView mv = new ModelAndView("vaga/listaVagas");;
        Iterable<Vaga>vagas = vr.findAll();
        mv.addObject("vagas", vagas);
        return mv;
    }

    // LISTA CANDIDATOS DE UMA VAGA
    @RequestMapping(value = "/{codigo}", method = RequestMethod.GET)
    public ModelAndView detalhesVaga(@PathVariable("codigo") Long codigo){

        Vaga vaga = vr.findByCodigo(codigo);
        ModelAndView mv = new ModelAndView("vaga/detalhesVaga");
        mv.addObject("vaga", vaga);
        Iterable<Candidatos> candidatos = cr.findByVaga(vaga);
        mv.addObject("candidatos", candidatos);
        return mv;

    }

    // DELETA VAGA
    @RequestMapping(value = "/deletarVaga/{codigo}", method = RequestMethod.DELETE)
    public String deleteVaga(@PathVariable("codigo") Long codigo){

        Vaga vaga = vr.findByCodigo(codigo);
        vr.delete(vaga);
        return "redirect:/vagas";
    }

    public String detalheVagaPost(@PathVariable("codigo") Long codigo, @Valid Candidatos candidatos, BindingResult result, RedirectAttributes attributes){
        if(result.hasErrors()){
            attributes.addFlashAttribute("mensagem", "Verifica os campos...");
            return "redirect:/{codigo}";
        }

        // TESTE CONSISTENCIA -> rg duplicado
        if(cr.findByRg(candidatos.getRg()) != null){
            attributes.addFlashAttribute("mensagem", "Rg Duplicado...");
            return "redirect:/{codigo}";
        }
        Vaga vaga = vr.findByCodigo(codigo);
        candidatos.setVaga(vaga);
        cr.save(candidatos);
        attributes.addFlashAttribute("mensagem", "Candidato cadastrado com sucesso...");
        return "redirect:/{codigo}";
    }
}
